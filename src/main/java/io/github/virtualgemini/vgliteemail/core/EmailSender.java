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

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.api.impl.AbstractEmailSender;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicy;

import java.util.concurrent.Executor;

/**
 * 开箱即用的“默认发送器”。
 * 内部持有一个 EmailChannel（真正干活的），其余逻辑全部复用抽象类。
 */
public class EmailSender extends AbstractEmailSender {

    public EmailSender(IEmailChannel channel,
                       Executor executor,
                       RetryPolicy retryPolicy) {
        super(channel, executor, retryPolicy);   // 传给父类
    }

    @Override
    protected SendResponse tryOnce(SendRequest request) {
        return channel.send(request);   // 直接复用父类 protected channel
    }

    @Override
    protected boolean retryable(int errorCode) {
        return channel.retryable(errorCode);
    }

    public static EmailSender create(IEmailChannel channel,
                                     Executor executor,
                                     RetryPolicy retryPolicy) {
        return new EmailSender(channel, executor, retryPolicy);
    }
}