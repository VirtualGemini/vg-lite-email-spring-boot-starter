/*
 * Copyright 2025 Your VirtualGemini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.virtualgemini.vgliteemail.properties;

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: 重试策略 / Retry policy
 * @createDate 2025/11/21 21:46
 */

/**
 * 重试策略 / Retry policy
 */

@ConfigurationProperties(prefix = "vg.lite-email.retries")
public class RetryPolicyProperties {

    private int globalRetries = 1;
    private int maxRetries = 1;

    /**
     * 初始延迟（ms）
     */
    private long initialDelay = 1000;

    /* ========== Getter & Setter ========== */
    public int getGlobalRetries() { return globalRetries; }
    public int getMaxRetries() { return maxRetries; }
    public void setGlobalRetries(int globalRetries) { this.globalRetries = globalRetries; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }

    public long getInitialDelay() { return initialDelay; }
    public void setInitialDelay(long initialDelay) { this.initialDelay = initialDelay; }

    /* ========== 核心方法 ========== */
    public boolean shouldRetry(int errorCode, int tried) {
        return tried < maxRetries
                && IEmailChannel.DEFAULT_RETRYABLE.contains(errorCode);
    }

    public long nextDelay(int tried) {
        return initialDelay * (1L << tried); // 指数退避
    }
}

