package com.jflux.model;

import java.util.List;
import java.util.Map;

public record HttpResponseData(
        int statusCode,
        Map<String, List<String>> headers,
        byte[] body
) {
}