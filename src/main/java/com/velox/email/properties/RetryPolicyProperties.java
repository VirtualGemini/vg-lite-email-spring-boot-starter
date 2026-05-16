package com.velox.email.properties;

import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "velox.email.retry")
public class RetryPolicyProperties {

    private boolean enabled = true;
    private int defaultAttempts = 1;
    private int maxAttempts = 3;
    private Duration initialDelay = Duration.ofSeconds(1);
    private double multiplier = 2.0;
    private Duration maxDelay = Duration.ofSeconds(30);

    public void validate() {
        if (defaultAttempts < 1) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_RETRY_DEFAULT_ATTEMPTS_INVALID);
        }
        if (maxAttempts < 1) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_RETRY_MAX_ATTEMPTS_INVALID);
        }
        if (multiplier < 1.0d) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_RETRY_MULTIPLIER_INVALID);
        }
        if (initialDelay.isNegative()) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_RETRY_INITIAL_DELAY_INVALID);
        }
        if (maxDelay.isNegative()) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_RETRY_MAX_DELAY_INVALID);
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
