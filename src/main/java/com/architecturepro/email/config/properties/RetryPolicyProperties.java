package com.architecturepro.email.config.properties;

import com.architecturepro.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "architecture.email.retry")
public class RetryPolicyProperties {

    private boolean enabled = true;
    private int defaultAttempts = 1;
    private int maxAttempts = 3;
    private Duration initialDelay = Duration.ofSeconds(1);
    private double multiplier = 2.0;
    private Duration maxDelay = Duration.ofSeconds(30);

    public void validate() {
        if (defaultAttempts < 1) {
            throw new EmailConfigException("architecture.email.retry.default-attempts must be >= 1");
        }
        if (maxAttempts < 1) {
            throw new EmailConfigException("architecture.email.retry.max-attempts must be >= 1");
        }
        if (multiplier < 1.0d) {
            throw new EmailConfigException("architecture.email.retry.multiplier must be >= 1.0");
        }
        if (initialDelay.isNegative()) {
            throw new EmailConfigException("architecture.email.retry.initial-delay must be >= 0");
        }
        if (maxDelay.isNegative()) {
            throw new EmailConfigException("architecture.email.retry.max-delay must be >= 0");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDefaultAttempts() {
        return defaultAttempts;
    }

    public void setDefaultAttempts(int defaultAttempts) {
        this.defaultAttempts = defaultAttempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Duration initialDelay) {
        this.initialDelay = initialDelay;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Duration getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(Duration maxDelay) {
        this.maxDelay = maxDelay;
    }
}
