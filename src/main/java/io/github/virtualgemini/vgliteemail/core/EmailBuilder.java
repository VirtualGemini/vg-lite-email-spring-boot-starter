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

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/21 22:01
 */

import io.github.virtualgemini.vgliteemail.config.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一键链式构建器 / One-click fluent builder
 * 零配置即可用
 */
public class EmailBuilder {

    private final LiteEmailSender sender;
    private String to;
    private String subject;
    private String text;
    private boolean html = false;
    private boolean async = false;   // 状态标记
    private int retry = 1;           // 默认 1 次（不重试）

    private static final Logger log = LoggerFactory.getLogger(EmailBuilder.class);

    public EmailBuilder(LiteEmailSender sender) { this.sender = sender;
    }


    /* ---------- 链式 setter ---------- */
    public EmailBuilder to(String to) { this.to = to; return this; }
    public EmailBuilder subject(String subject) { this.subject = subject; return this; }
    public EmailBuilder text(String text) { this.text = text; return this; }
    public EmailBuilder html(boolean html) { this.html = html; return this; }

    /* ---------- 一键开关 ---------- */
    public EmailBuilder async() { this.async = true; return this; }
    public EmailBuilder retry(int times) {
        this.retry = times;
        return this;
    }
    public EmailBuilder retry() {
        return this.retry(this.retry);
    }  // 默认重试 retry 次

    /* ========== 核心魔法：同一 send() 返回不同类型 ========== */
    @SuppressWarnings("unchecked")
    public <T> T send() {
        SendRequest req = SendRequest.builder()
                .to(to).subject(subject).text(text).html(html).build();
        sender.setRetryTimes(retry);
        if (async) {
            System.out.println("异步发送邮件");
            return (T) sender.sendAsync(req);   // 返回 CompletableFuture
        } else {
            System.out.println("同步发送邮件");
            return (T) sender.send(req);        // 返回 SendResponse
        }
    }

}
