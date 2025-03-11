package com.example.ratelimiter.exception;

public class RuleParsingException extends RuntimeException {

    public RuleParsingException(String message) {
        super(message);
    }

    public RuleParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
