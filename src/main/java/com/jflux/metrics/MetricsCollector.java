package com.jflux.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MetricsCollector {
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicLong rateLimited = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> backendHits = new ConcurrentHashMap<>();

    public void recordRequest() {
        totalRequests.incrementAndGet();
    }

    public void recordCacheHit() {
        cacheHits.incrementAndGet();
    }

    public void recordCacheMiss() {
        cacheMisses.incrementAndGet();
    }

    public void recordRateLimited() {
        rateLimited.incrementAndGet();
    }

    public void recordFailedRequest() {
        failedRequests.incrementAndGet();
    }

    public void recordBackendSuccess(String backendUrl) {
        backendHits.computeIfAbsent(backendUrl, k -> new AtomicLong(0)).incrementAndGet();
    }

    public String toJsonReport() {
        StringBuilder backendsJson = new StringBuilder("{");
        var entries = backendHits.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, AtomicLong> entry = entries.next();
            backendsJson.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().get());
            if (entries.hasNext()) {
                backendsJson.append(", ");
            }
        }
        backendsJson.append("}");

        return "{\n" +
                "  \"totalRequests\": " + totalRequests.get() + ",\n" +
                "  \"cacheHits\": " + cacheHits.get() + ",\n" +
                "  \"cacheMisses\": " + cacheMisses.get() + ",\n" +
                "  \"rateLimited429\": " + rateLimited.get() + ",\n" +
                "  \"failed503\": " + failedRequests.get() + ",\n" +
                "  \"backendSuccess\": " + backendsJson + "\n" +
                "}\n";
    }
}