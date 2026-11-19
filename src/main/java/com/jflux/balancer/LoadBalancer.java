package com.jflux.balancer;

import com.jflux.model.BackendServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private final List<BackendServer> backends;
    private final AtomicInteger index = new AtomicInteger(0);
    private final ScheduledExecutorService healthChecker = Executors.newSingleThreadScheduledExecutor();

    public LoadBalancer(List<String> urls) {
        this.backends = new ArrayList<>();
        for (String url : urls) {
            this.backends.add(new BackendServer(url));
        }
    }

    public void startHealthChecks(long intervalMillis, int timeoutMillis) {
        healthChecker.scheduleAtFixedRate(() -> {
            for (BackendServer server : backends) {
                boolean reachable = checkTcpReachable(
                        server.getUri().getHost(),
                        resolvePort(server),
                        timeoutMillis
                );
                boolean previous = server.isHealthy();
                server.setHealthy(reachable);

                if (previous != reachable) {
                    System.out.println("[HEALTH] " + server.getUrl() + " -> " + (reachable ? "UP" : "DOWN"));
                }
            }
        }, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
    }

    public BackendServer nextServer() {
        List<BackendServer> healthyServers = backends.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            return null;
        }

        int currentIdx = Math.floorMod(index.getAndIncrement(), healthyServers.size());
        return healthyServers.get(currentIdx);
    }

    public List<BackendServer> getBackends() {
        return List.copyOf(backends);
    }

    public void shutdown() {
        healthChecker.shutdownNow();
    }

    private int resolvePort(BackendServer server) {
        int port = server.getUri().getPort();
        if (port != -1) {
            return port;
        }
        return "https".equalsIgnoreCase(server.getUri().getScheme()) ? 443 : 80;
    }

    private boolean checkTcpReachable(String host, int port, int timeoutMillis) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMillis);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}