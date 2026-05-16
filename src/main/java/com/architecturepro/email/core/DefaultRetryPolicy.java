package com.architecturepro.email.core;

import com.architecturepro.email.config.properties.RetryPolicyProperties;

import java.time.Duration;

public class DefaultRetryPolicy implements RetryPolicy {

    private final RetryPolicyProperties properties;

    public DefaultRetryPolicy(RetryPolicyProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean shouldRetry(SendRequest request, SendResponse response, int attempt) {
        if (!properties.isEnabled()) {
            return false;
        }
        return attempt < resolveMaxAttempts(request);
    }

    @Override
    public Duration nextDelay(SendRequest request, SendResponse response, int attempt) {
        long delay = Math.round(properties.getInitialDelay().toMillis() * Math.pow(properties.getMultiplier(), Math.max(0, attempt - 1)));
        long boundedDelay = Math.min(delay, properties.getMaxDelay().toMillis());
        return Duration.ofMillis(Math.max(0, boundedDelay));
    }

    private int resolveMaxAttempts(SendRequest request) {
        int requested = request.maxAttempts();
        int desired = requested > 0 ? requested : properties.getDefaultAttempts();
        return Math.max(1, Math.min(desired, properties.getMaxAttempts()));
    }
}
