package com.velox.email.properties;

import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "velox.email.async")
public class EmailAsyncProperties {

    private static final String DEFAULT_THREAD_NAME_PREFIX = "velox-email-";

    private boolean enabled = true;
    private boolean virtualThreads = true;
    private int concurrencyLimit = 256;
    private String threadNamePrefix = DEFAULT_THREAD_NAME_PREFIX;

    public void validate() {
        if (concurrencyLimit < 1) {
            throw new EmailConfigException(EmailCommonMessages.EMAIL_ASYNC_CONCURRENCY_LIMIT_INVALID);
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
