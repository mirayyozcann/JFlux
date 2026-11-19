package com.jflux;

import com.jflux.config.ProxyConfig;
import com.jflux.server.ProxyServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ProxyConfig config = ProxyConfig.load();
        ProxyServer server = new ProxyServer(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down JFlux Reverse Proxy...");
            server.stop();
        }));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start proxy server: " + e.getMessage());
        }
    }
}