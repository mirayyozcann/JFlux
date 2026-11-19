package com.jflux.server;

import com.jflux.balancer.LoadBalancer;
import com.jflux.cache.ResponseCache;
import com.jflux.config.ProxyConfig;
import com.jflux.metrics.MetricsCollector;
import com.jflux.ratelimit.RateLimiter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProxyServer {
    private final ProxyConfig config;
    private final LoadBalancer loadBalancer;
    private final ResponseCache cache;
    private final RateLimiter rateLimiter;
    private final MetricsCollector metrics;
    private final ExecutorService executor;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public ProxyServer(ProxyConfig config) {
        this.config = config;
        this.loadBalancer = new LoadBalancer(config.backendUrls());
        this.cache = new ResponseCache(config.cacheTtlMillis());
        this.rateLimiter = new RateLimiter(config.maxRequestsPerMinute());
        this.metrics = new MetricsCollector();
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start() throws IOException {
        loadBalancer.startHealthChecks(config.healthCheckIntervalMillis(), 1000);
        serverSocket = new ServerSocket(config.proxyPort());
        running = true;

        System.out.println("JFlux Reverse Proxy listening on http://localhost:" + config.proxyPort());
        System.out.println("Backends: " + config.backendUrls());
        System.out.println("Metrics endpoint: http://localhost:" + config.proxyPort() + "/_jflux/metrics");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(
                        clientSocket,
                        loadBalancer,
                        cache,
                        rateLimiter,
                        metrics,
                        config.maxRetries()
                ));
            } catch (IOException e) {
                if (running) {
                    System.out.println("[ERROR] Accept loop failed: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        loadBalancer.shutdown();
        executor.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}