package com.example.ratelimiter.model;

import lombok.Getter;
import lombok.Setter;

import java.time.temporal.ChronoUnit;

@Setter
@Getter
public class ApiRule extends Rule {
    private String apiPath;
    private String method;

    public ApiRule(String id, String resourceId, Algorithm algorithm, long limit, long period,
                   ChronoUnit timeUnit, String apiPath, String method) {
        super(id, resourceId, algorithm, limit, period, timeUnit);
        this.apiPath = apiPath;
        this.method = method;
    }

}