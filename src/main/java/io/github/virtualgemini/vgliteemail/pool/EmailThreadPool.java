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
package io.github.virtualgemini.vgliteemail.pool;

import io.github.virtualgemini.vgliteemail.properties.EmailAsyncProperties;
import io.github.virtualgemini.vgliteemail.utils.LiteMailLog;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: Lightweight thread pool
 * @createDate 2025/11/21 22:00
 */
// 轻量线程池 / Lightweight thread pool
@Configuration
@EnableConfigurationProperties(EmailAsyncProperties.class)
public class EmailThreadPool {

    @Bean(name = "VGEmailExecutor")
    public Executor emailExecutor(EmailAsyncProperties p) {
        p.validate(); // 校验线程池配置
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(p.getCorePoolSize());
        exec.setMaxPoolSize(p.getMaxPoolSize());
        exec.setQueueCapacity(p.getQueueCapacity());
        exec.setKeepAliveSeconds(p.getKeepAliveSeconds());
        exec.setThreadNamePrefix(p.getThreadNamePrefix());

        // 1. 拒绝策略
        exec.setRejectedExecutionHandler(toHandler(p.getRejectedPolicy()));

        // 2. 优雅停机
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setAwaitTerminationSeconds(p.getAwaitTerminationSeconds()); // <-- 可配置

        LiteMailLog.info("LiteEmail", "Initialized email executor with core={}, max={}, queue={}, policy={}",
                p.getCorePoolSize(), p.getMaxPoolSize(), p.getQueueCapacity(), p.getRejectedPolicy());

        exec.initialize();
        return exec;
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

}
