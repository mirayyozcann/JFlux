package com.jflux.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public record ProxyConfig(
        int proxyPort,
        List<String> backendUrls,
        long cacheTtlMillis,
        int maxRequestsPerMinute,
        long healthCheckIntervalMillis,
        int maxRetries
) {
    public static ProxyConfig defaultConfig() {
        return new ProxyConfig(
                8080,
                List.of(
                        "http://localhost:9001",
                        "http://localhost:9002",
                        "http://localhost:9003"
                ),
                10_000L,
                30,
                5_000L,
                3
        );
    }

    public static ProxyConfig load() {
        ProxyConfig defaults = defaultConfig();
        Properties props = new Properties();

        Path externalPath = Path.of("config.properties");
        if (Files.exists(externalPath)) {
            try (InputStream in = Files.newInputStream(externalPath)) {
                props.load(in);
                return fromProperties(props, defaults);
            } catch (IOException e) {
                System.out.println("[WARN] Failed to read external config.properties, using defaults: " + e.getMessage());
            }
        }

        try (InputStream in = ProxyConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
                return fromProperties(props, defaults);
            }
        } catch (IOException e) {
            System.out.println("[WARN] Failed to load classpath config.properties, using defaults: " + e.getMessage());
        }

        return defaults;
    }

    private static ProxyConfig fromProperties(Properties props, ProxyConfig defaults) {
        int port = parseIntOrDefault(props.getProperty("proxy.port"), defaults.proxyPort());
        long ttl = parseLongOrDefault(props.getProperty("cache.ttl.millis"), defaults.cacheTtlMillis());
        int rateLimit = parseIntOrDefault(props.getProperty("ratelimit.max.per.minute"), defaults.maxRequestsPerMinute());
        long healthInterval = parseLongOrDefault(props.getProperty("healthcheck.interval.millis"), defaults.healthCheckIntervalMillis());
        int retries = parseIntOrDefault(props.getProperty("proxy.max.retries"), defaults.maxRetries());

        String rawUrls = props.getProperty("backend.urls");
        List<String> urls = defaults.backendUrls();
        if (rawUrls != null && !rawUrls.isBlank()) {
            List<String> parsed = Arrays.stream(rawUrls.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            if (!parsed.isEmpty()) {
                urls = parsed;
            }
        }

        return new ProxyConfig(port, urls, ttl, rateLimit, healthInterval, retries);
    }

    private static int parseIntOrDefault(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static long parseLongOrDefault(String raw, long fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}