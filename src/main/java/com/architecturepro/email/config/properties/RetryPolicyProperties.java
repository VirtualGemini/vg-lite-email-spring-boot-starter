package com.architecturepro.email.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vg.lite-email.retries")
public class RetryPolicyProperties {

    private int globalRetries = 1;
    private int maxRetries = 1;
    private long initialDelay = 1000;

    public boolean shouldRetry(int tried) {
        return tried < maxRetries;
    }

    public long nextDelay(int tried) {
        return initialDelay * (1L << Math.max(tried, 0));
    }

    public int getGlobalRetries() {
        return globalRetries;
    }

    public void setGlobalRetries(int globalRetries) {
        this.globalRetries = globalRetries;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }
}
