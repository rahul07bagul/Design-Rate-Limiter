package com.example.ratelimiter.model;

import java.time.temporal.ChronoUnit;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public ChronoUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(ChronoUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}