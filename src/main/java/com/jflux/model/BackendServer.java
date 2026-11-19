package com.jflux.model;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackendServer {
    private final String url;
    private final URI uri;
    private final AtomicBoolean healthy;

    public BackendServer(String url) {
        this.url = url;
        this.uri = URI.create(url);
        this.healthy = new AtomicBoolean(true);
    }

    public String getUrl() {
        return url;
    }

    public URI getUri() {
        return uri;
    }

    public boolean isHealthy() {
        return healthy.get();
    }

    public void setHealthy(boolean status) {
        healthy.set(status);
    }

    @Override
    public String toString() {
        return url + " [" + (healthy.get() ? "UP" : "DOWN") + "]";
    }
}