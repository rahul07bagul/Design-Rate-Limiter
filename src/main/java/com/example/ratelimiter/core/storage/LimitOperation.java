package com.example.ratelimiter.core.storage;

public interface LimitOperation {
    /**
     * Get the current value for a key
     */
    Long getValue(String key);

    /**
     * Get string value for a key
     */
    String getStringValue(String key);

    /**
     * Set value for a key
     */
    void setValue(String key, String value);

    /**
     * Increment a counter and get the new value
     */
    Long incrementAndGet(String key, Long incrementBy);

    /**
     * Set expiry time for a key in seconds
     */
    void setExpiry(String key, long seconds);
}
