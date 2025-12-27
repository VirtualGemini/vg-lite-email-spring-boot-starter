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
package io.github.virtualgemini.vgliteemail.config.properties;

import io.github.virtualgemini.vgliteemail.enums.RejectedPolicyEnum;
import io.github.virtualgemini.vgliteemail.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: Email async configuration properties
 * @createDate 2025/11/22 11:16
 */
@Component
@ConfigurationProperties(prefix = "vg.lite-email.async")
public class EmailAsyncProperties {
    private int corePoolSize = 4;
    private int maxPoolSize = 8;
    private int queueCapacity = 200;
    private String rejectedPolicy = "CALLER_RUNS";   // 默认为推荐策略
    private int keepAliveSeconds = 60;   // 默认 60 秒
    private int awaitTerminationSeconds = 30; // 默认 30 秒
    private String threadNamePrefix = "vg-email-";

    public void validate() {
        if (corePoolSize < 1) {
            throw new EmailConfigException("corePoolSize must be >= 1");
        }
        if (maxPoolSize < corePoolSize) {
            throw new EmailConfigException("maxPoolSize must be >= corePoolSize");
        }
        if (queueCapacity < 0) {
            throw new EmailConfigException("queueCapacity must be >= 0");
        }
        if (keepAliveSeconds < 0) {
            throw new EmailConfigException("keepAliveSeconds must be >= 0");
        }
        if (awaitTerminationSeconds < 0) {
            throw new EmailConfigException("awaitTerminationSeconds must be >= 0");
        }
        if (!RejectedPolicyEnum.isValid(rejectedPolicy)) {
            throw new EmailConfigException("Invalid async.rejectedPolicy: " + rejectedPolicy
                    + ". Allowed values: CALLER_RUNS, ABORT, DISCARD, DISCARD_OLDEST.");
        }
    }

    public Executor getEmailExecutor() {
        validate(); // 校验配置

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(threadNamePrefix);

        // 设置拒绝策略
        executor.setRejectedExecutionHandler(toHandler(rejectedPolicy));

        // 优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);

        executor.initialize();
        return executor;
    }

    private RejectedExecutionHandler toHandler(String policy) {
        if (policy == null) return new ThreadPoolExecutor.CallerRunsPolicy();
        switch (policy.toUpperCase()) {
            case "CALLER_RUNS":    return new ThreadPoolExecutor.CallerRunsPolicy();
            case "ABORT":          return new ThreadPoolExecutor.AbortPolicy();
            case "DISCARD":        return new ThreadPoolExecutor.DiscardPolicy();
            case "DISCARD_OLDEST": return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:               return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }

    /* ---------- 构造器 ---------- */
    public EmailAsyncProperties() {
    }

    /* ---------- getter / setter ---------- */

    public int getAwaitTerminationSeconds() {
        return awaitTerminationSeconds;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public String getRejectedPolicy() {
        return rejectedPolicy;
    }

    public void setRejectedPolicy(String rejectedPolicy) {
        this.rejectedPolicy = rejectedPolicy;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
}