package com.example.ratelimiter.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RateLimitRequest {
    private String resourceId;
    private String clientId;

    public RateLimitRequest() {}

    public RateLimitRequest(String resourceId, String clientId) {
        this.resourceId = resourceId;
        this.clientId = clientId;
    }

}
