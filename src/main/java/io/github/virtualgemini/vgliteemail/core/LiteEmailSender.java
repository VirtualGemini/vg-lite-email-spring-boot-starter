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
package io.github.virtualgemini.vgliteemail.core;

import io.github.virtualgemini.vgliteemail.config.RetryPolicy;
import io.github.virtualgemini.vgliteemail.event.EmailSendInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: 核心发送器 / Core sender
 * @createDate 2025/11/21 21:47
 */


/**
 * 核心发送器 / Core sender
 */
public class LiteEmailSender {
    private final Channel channel;
    private final Executor executor;
    private final RetryPolicy retryPolicy;
    private volatile int retryTimes = 1;   // 默认 1 次（不重试）
    private final List<EmailSendInterceptor> interceptors = new CopyOnWriteArrayList<>();

    /* 构造器 / constructor */
    public LiteEmailSender(Channel channel, Executor executor, RetryPolicy retryPolicy) {
        this.channel = channel;
        this.executor = executor;
        this.retryPolicy = retryPolicy;
    }

    /* 注册拦截器 / interceptor registration */
    public void addInterceptor(EmailSendInterceptor interceptor) {
        if (interceptor != null) interceptors.add(interceptor);
    }

    /* 对外只留一个方法 / only one public method */
    public SendResponse send(String to, String subject, String text) {
        return send(SendRequest.builder().to(to).subject(subject).text(text).build());
    }

    /* 链式入口 / fluent entry */
    public EmailBuilder create() { return new EmailBuilder(this); }

    /* 实例级重试设置 / per-instance retry */
    public void setRetryTimes(int retryTimes) { this.retryTimes = Math.max(1, retryTimes); }
    public void setRetryTimes() {}

    /* 内部发送（带重试）/ internal send with retry */
    public SendResponse send(SendRequest req) {
        if (retryPolicy.getMaxRetries() != 1) {
            if (retryPolicy.getMaxRetries() < retryTimes) {
                retryTimes = retryPolicy.getMaxRetries();
            }
        }
        // 统一 before 钩子 / before hook
        for (EmailSendInterceptor in : interceptors) in.beforeSend(req);

        int tried = 0;
        SendResponse resp;
        do {
            resp = channel.send(req);
            if (resp.isSuccess()) break;   // 成功也跳出，但不再立即 return
            if (!channel.retryable(resp.getErrorCode())) break;
            tried++;
            long delay = retryPolicy.nextDelay(tried);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            System.out.println("第 " + (tried + 1) + " 次，errorCode=" + resp.getErrorCode() + ", success=" + resp.isSuccess()); // TODO: 日志
        } while (tried < retryTimes);

        // 统一 after 钩子 / after hook（无论成功失败）
        for (EmailSendInterceptor in : interceptors) in.afterSend(req, resp);
        return resp; // 最后一次结果（成功或失败）
    }
    /* 异步发送 / async send */
    public CompletableFuture<SendResponse> sendAsync (SendRequest req){
        return CompletableFuture.supplyAsync(() -> send(req), executor);
    }

}
