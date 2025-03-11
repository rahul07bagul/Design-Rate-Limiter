package com.example.ratelimiter.util;

import org.springframework.stereotype.Component;

@Component
public class KeyGenerator {

    private static final String KEY_SEPARATOR = ":";
    private static final String KEY_PREFIX = "rate-limit";

    public String generateKey(String resourceId, String clientId) {
        return KEY_PREFIX + KEY_SEPARATOR + resourceId + KEY_SEPARATOR + clientId;
    }
}
