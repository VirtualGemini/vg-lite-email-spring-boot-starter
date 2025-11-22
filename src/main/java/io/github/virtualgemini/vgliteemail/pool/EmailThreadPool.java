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

import io.github.virtualgemini.vgliteemail.config.EmailAsyncProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: Lightweight thread pool
 * @createDate 2025/11/21 22:00
 */
// 轻量线程池 / Lightweight thread pool
public class EmailThreadPool {

    @Bean
    public Executor emailExecutor(EmailAsyncProperties p) {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(p.getCorePoolSize());
        exec.setMaxPoolSize(p.getMaxPoolSize());
        exec.setQueueCapacity(p.getQueueCapacity());
        exec.setThreadNamePrefix(p.getThreadNamePrefix());
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

}
