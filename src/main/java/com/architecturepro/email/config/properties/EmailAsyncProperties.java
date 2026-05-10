package com.architecturepro.email.config.properties;

import com.architecturepro.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vg.lite-email.async")
public class EmailAsyncProperties {

    private boolean enabled = true;
    private boolean virtualThreads = true;
    private int concurrencyLimit = 256;
    private String threadNamePrefix = "vg-email-";

    public void validate() {
        if (concurrencyLimit < 1) {
            throw new EmailConfigException("vg.lite-email.async.concurrency-limit must be >= 1");
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
