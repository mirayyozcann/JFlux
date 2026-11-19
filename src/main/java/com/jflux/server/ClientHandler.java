package com.jflux.server;

import com.jflux.balancer.LoadBalancer;
import com.jflux.cache.ResponseCache;
import com.jflux.metrics.MetricsCollector;
import com.jflux.model.BackendServer;
import com.jflux.model.HttpResponseData;
import com.jflux.ratelimit.RateLimiter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ClientHandler implements Runnable {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private final Socket clientSocket;
    private final LoadBalancer loadBalancer;
    private final ResponseCache cache;
    private final RateLimiter rateLimiter;
    private final MetricsCollector metrics;
    private final int maxRetries;

    public ClientHandler(
            Socket clientSocket,
            LoadBalancer loadBalancer,
            ResponseCache cache,
            RateLimiter rateLimiter,
            MetricsCollector metrics,
            int maxRetries
    ) {
        this.clientSocket = clientSocket;
        this.loadBalancer = loadBalancer;
        this.cache = cache;
        this.rateLimiter = rateLimiter;
        this.metrics = metrics;
        this.maxRetries = Math.max(1, maxRetries);
    }

    @Override
    public void run() {
        metrics.recordRequest();
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        try (Socket socket = clientSocket;
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            if (!rateLimiter.allowRequest(clientIp)) {
                metrics.recordRateLimited();
                sendSimpleResponse(out, 429, "Too Many Requests", "Rate limit exceeded.\n", "text/plain; charset=UTF-8");
                return;
            }

            ParsedRequest request = readHttpRequest(in);
            if (request == null || request.method() == null || request.path() == null) {
                sendSimpleResponse(out, 400, "Bad Request", "Malformed HTTP request.\n", "text/plain; charset=UTF-8");
                return;
            }

            String method = request.method().toUpperCase(Locale.ROOT);
            String path = request.path();

            if ("GET".equals(method) && "/_jflux/metrics".equals(path)) {
                sendSimpleResponse(out, 200, "OK", metrics.toJsonReport(), "application/json; charset=UTF-8");
                return;
            }

            String cacheKey = method + ":" + path;

            if ("GET".equals(method)) {
                HttpResponseData cached = cache.get(cacheKey);
                if (cached != null) {
                    metrics.recordCacheHit();
                    System.out.println("[CACHE HIT] " + method + " " + path);
                    writeHttpResponse(out, cached, "HIT");
                    return;
                }
            }

            metrics.recordCacheMiss();
            ForwardResult result = forwardWithRetry(method, path, request.body());
            if (result.response() == null) {
                metrics.recordFailedRequest();
                sendSimpleResponse(out, 503, "Service Unavailable", "All backend attempts failed.\n", "text/plain; charset=UTF-8");
                return;
            }

            metrics.recordBackendSuccess(result.server().getUrl());
            HttpResponseData backendResponse = result.response();
            if ("GET".equals(method) && backendResponse.statusCode() >= 200 && backendResponse.statusCode() < 300) {
                cache.put(cacheKey, backendResponse);
            }

            System.out.println("[FORWARD] " + method + " " + path + " -> " + result.server().getUrl()
                    + " (" + backendResponse.statusCode() + ", attempts=" + result.attempts() + ")");
            writeHttpResponse(out, backendResponse, "MISS");

        } catch (IOException e) {
            System.out.println("[ERROR] Client handling failed (" + clientIp + "): " + e.getMessage());
        }
    }

    private ForwardResult forwardWithRetry(String method, String path, byte[] body) {
        Set<String> triedUrls = new HashSet<>();
        int attempts = 0;

        for (int i = 0; i < maxRetries; i++) {
            BackendServer target = loadBalancer.nextServer();
            if (target == null) {
                break;
            }

            if (triedUrls.contains(target.getUrl()) && triedUrls.size() < loadBalancer.getBackends().size()) {
                continue;
            }

            attempts++;
            triedUrls.add(target.getUrl());

            try {
                HttpResponseData response = sendSingleRequest(target, method, path, body);
                return new ForwardResult(target, response, attempts);
            } catch (IOException e) {
                target.setHealthy(false);
                System.out.println("[FAILOVER] Attempt " + attempts + " failed on " + target.getUrl()
                        + " (" + e.getMessage() + "), marking DOWN and retrying...");
            }
        }

        return new ForwardResult(null, null, attempts);
    }

    private HttpResponseData sendSingleRequest(BackendServer target, String method, String path, byte[] body) throws IOException {
        URI targetUri = URI.create(target.getUrl() + path);
        HttpRequest.BodyPublisher publisher = (body != null && body.length > 0)
                ? HttpRequest.BodyPublishers.ofByteArray(body)
                : HttpRequest.BodyPublishers.noBody();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(targetUri)
                .timeout(Duration.ofSeconds(3))
                .method(method, publisher);

        try {
            HttpResponse<byte[]> response = HTTP_CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            Map<String, List<String>> cleanHeaders = new LinkedHashMap<>();
            response.headers().map().forEach((k, v) -> {
                if (k != null && !k.equalsIgnoreCase("Transfer-Encoding") && !k.equalsIgnoreCase("Content-Length")) {
                    cleanHeaders.put(k, v);
                }
            });
            return new HttpResponseData(response.statusCode(), cleanHeaders, response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while forwarding request", e);
        }
    }

    private ParsedRequest readHttpRequest(InputStream in) throws IOException {
        ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
        int prev = -1;
        int cur;
        int crlfCount = 0;

        while ((cur = in.read()) != -1) {
            headerBuf.write(cur);
            if (prev == '\r' && cur == '\n') {
                crlfCount++;
                if (crlfCount == 2) {
                    break;
                }
            } else if (cur != '\r') {
                crlfCount = 0;
            }
            prev = cur;
        }

        String rawHeaders = headerBuf.toString(StandardCharsets.ISO_8859_1);
        if (rawHeaders.isBlank()) {
            return null;
        }

        String[] lines = rawHeaders.split("\r\n");
        String requestLine = lines[0];
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            return null;
        }

        int contentLength = 0;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int colonIdx = line.indexOf(':');
            if (colonIdx > 0) {
                String name = line.substring(0, colonIdx).trim();
                String val = line.substring(colonIdx + 1).trim();
                if ("Content-Length".equalsIgnoreCase(name)) {
                    try {
                        contentLength = Integer.parseInt(val);
                    } catch (NumberFormatException ignored) {
                        contentLength = 0;
                    }
                }
            }
        }

        byte[] body = new byte[0];
        if (contentLength > 0) {
            body = in.readNBytes(contentLength);
        }

        return new ParsedRequest(parts[0], parts[1], body);
    }

    private void writeHttpResponse(OutputStream out, HttpResponseData response, String cacheStatus) throws IOException {
        byte[] body = response.body() != null ? response.body() : new byte[0];
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(response.statusCode()).append(" ").append(reasonPhrase(response.statusCode())).append("\r\n");
        sb.append("X-Proxy-Cache: ").append(cacheStatus).append("\r\n");
        sb.append("Content-Length: ").append(body.length).append("\r\n");

        response.headers().forEach((name, values) -> {
            for (String value : values) {
                sb.append(name).append(": ").append(value).append("\r\n");
            }
        });

        sb.append("Connection: close\r\n\r\n");
        out.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        out.write(body);
        out.flush();
    }

    private void sendSimpleResponse(OutputStream out, int statusCode, String reason, String message, String contentType) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + statusCode + " " + reason + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + bytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        out.write(headers.getBytes(StandardCharsets.ISO_8859_1));
        out.write(bytes);
        out.flush();
    }

    private String reasonPhrase(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 429 -> "Too Many Requests";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            default -> "OK";
        };
    }

    private record ParsedRequest(String method, String path, byte[] body) {
    }

    private record ForwardResult(BackendServer server, HttpResponseData response, int attempts) {
    }
}