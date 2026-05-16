package com.velox.email.config.properties;

import com.velox.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "velox.email.async")
public class EmailAsyncProperties {

    private boolean enabled = true;
    private boolean virtualThreads = true;
    private int concurrencyLimit = 256;
    private String threadNamePrefix = "velox-email-";

    public void validate() {
        if (concurrencyLimit < 1) {
            throw new EmailConfigException("velox.email.async.concurrency-limit must be >= 1");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVirtualThreads() {
        return virtualThreads;
    }

    public void setVirtualThreads(boolean virtualThreads) {
        this.virtualThreads = virtualThreads;
    }

    public int getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public void setConcurrencyLimit(int concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
}
