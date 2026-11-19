package com.jflux.cache;

import com.jflux.model.HttpResponseData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseCache {
    private record CacheEntry(HttpResponseData response, long expiresAtMillis) {
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAtMillis;
        }
    }

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public ResponseCache(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public HttpResponseData get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key, entry);
            return null;
        }
        return entry.response();
    }

    public void put(String key, HttpResponseData response) {
        if (ttlMillis <= 0) {
            return;
        }
        long expiresAt = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry(response, expiresAt));
    }

    public void clearExpired() {
        cache.entrySet().removeIf(e -> e.getValue().isExpired());
    }

    public int size() {
        return cache.size();
    }
}