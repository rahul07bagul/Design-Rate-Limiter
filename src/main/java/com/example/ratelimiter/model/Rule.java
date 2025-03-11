package com.example.ratelimiter.model;

import lombok.Getter;
import lombok.Setter;

import java.time.temporal.ChronoUnit;

@Setter
@Getter
public class Rule {
    private String id;
    private String resourceId;
    private Algorithm algorithm;
    private long limit;
    private long period;
    private ChronoUnit timeUnit;


    public Rule(String id, String resourceId, Algorithm algorithm, long limit, long period, ChronoUnit timeUnit) {
        this.id = id;
        this.resourceId = resourceId;
        this.algorithm = algorithm;
        this.limit = limit;
        this.period = period;
        this.timeUnit = timeUnit;
    }

}