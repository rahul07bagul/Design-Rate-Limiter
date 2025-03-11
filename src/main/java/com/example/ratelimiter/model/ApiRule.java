package com.example.ratelimiter.model;

import java.time.temporal.ChronoUnit;

public class ApiRule extends Rule {
    private String apiPath;
    private String method;

    public ApiRule(String id, String resourceId, Algorithm algorithm, long limit, long period,
                   ChronoUnit timeUnit, String apiPath, String method) {
        super(id, resourceId, algorithm, limit, period, timeUnit);
        this.apiPath = apiPath;
        this.method = method;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}