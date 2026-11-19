package com.jflux.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static final long WINDOW_MILLIS = 60_000L;

    private static final class ClientWindow {
        long windowStartMillis;
        int requestCount;

        ClientWindow(long windowStartMillis, int requestCount) {
            this.windowStartMillis = windowStartMillis;
            this.requestCount = requestCount;
        }
    }

    private final int maxRequestsPerMinute;
    private final Map<String, ClientWindow> clientWindows = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    public boolean allowRequest(String clientKey) {
        if (maxRequestsPerMinute <= 0) {
            return true;
        }

        long now = System.currentTimeMillis();
        ClientWindow window = clientWindows.computeIfAbsent(
                clientKey,
                k -> new ClientWindow(now, 0)
        );

        synchronized (window) {
            if (now - window.windowStartMillis >= WINDOW_MILLIS) {
                window.windowStartMillis = now;
                window.requestCount = 0;
            }
            if (window.requestCount >= maxRequestsPerMinute) {
                return false;
            }
            window.requestCount++;
            return true;
        }
    }
}