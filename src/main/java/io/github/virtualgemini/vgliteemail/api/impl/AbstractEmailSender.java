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
package io.github.virtualgemini.vgliteemail.api.impl;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:08
 */

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.api.IEmailSender;
import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 把“重试 + 线程池”做成模板方法，子类只需要实现真正的一次发送。
 */
public abstract class AbstractEmailSender implements IEmailSender {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final IEmailChannel channel;   // 供子类直接用
    private final Executor executor;
    private final RetryPolicy retryPolicy;
    private volatile int retryTimes = 1;
    protected AbstractEmailSender(IEmailChannel channel,
                                  Executor executor,
                                  RetryPolicy retryPolicy) {
        this.channel = channel;
        this.executor = executor;
        this.retryPolicy = retryPolicy;
    }

    private LiteEmailProperties properties;


    /* ---------- 开放给子类/Builder 微调 ---------- */
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = Math.max(1, retryTimes);
    }

    /* ---------- 模板方法：重试逻辑 ---------- */
    @Override
    public SendResponse send(SendRequest req) {
        // 1. 判断开发者是否显式调用 retry(x)
        boolean developerSpecified = retryTimes > 1;
        // 2. 获取有效重试次数（开发者优先，其次全局默认）
        int effectiveRetries = developerSpecified
                ? retryTimes
                : retryPolicy.getGlobalRetries();
        // 3. 最终重试次数受 maxRetries 限制
        int maxRetries = retryPolicy.getMaxRetries();
        int finalRetries = Math.min(effectiveRetries, maxRetries);
        // 4. 如果配置导致 finalRetries < 1，则至少执行 1 次
        if (finalRetries < 1) {
            finalRetries = 1;
        }

        int tried = 0;
        SendResponse resp;

        do {
            resp = tryOnce(req);

            // 成功 → 立即退出
            if (resp.isSuccess()) {
                break;
            }
            // 是否继续重试（包含：错误码是否可重试 + 次数限制）
            if (!retryPolicy.shouldRetry(resp.getErrorCode(), tried)) {
                break;
            }

            tried++;

            long delay = retryPolicy.nextDelay(tried);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        } while (true);

        return resp;
    }


    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest req) {
        return CompletableFuture.supplyAsync(() -> send(req), executor);
    }

    /* ---------- 子类必须实现 ---------- */
    protected abstract SendResponse tryOnce(SendRequest request);

    /* ---------- 子类可覆盖：哪些错误码值得重试 ---------- */
    protected boolean retryable(int errorCode) {
        return channel.retryable(errorCode);
    }
}
