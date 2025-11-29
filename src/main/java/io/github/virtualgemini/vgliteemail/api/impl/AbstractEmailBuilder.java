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

import io.github.virtualgemini.vgliteemail.api.IEmailBuilder;
import io.github.virtualgemini.vgliteemail.api.IEmailSender;
import io.github.virtualgemini.vgliteemail.core.SendRequest;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:13
 */
public abstract class AbstractEmailBuilder<T extends AbstractEmailBuilder<T>>
        implements IEmailBuilder<T> {

    protected String to;
    protected String subject;
    protected String text;
    protected boolean html;
    protected int retry = 1;
    protected boolean async;

    protected abstract IEmailSender sender();

    @SuppressWarnings("unchecked")
    protected T self() { return (T) this; }

    @Override
    public T to(String to) { this.to = to; return self(); }
    @Override
    public T subject(String subject) { this.subject = subject; return self(); }
    @Override
    public T text(String text) { this.text = text; return self(); }
    @Override
    public T html(boolean html) { this.html = html; return self(); }
    @Override
    public T retry(int times) { this.retry = times; return self(); }
    @Override
    public T retry() { return self(); }
    @Override
    public T async() { this.async = true; return self(); }

    @Override
    public Object send() {
        SendRequest req = SendRequest.builder()
                .to(to).subject(subject).text(text).html(html).build();
        IEmailSender sender = sender();
        if (sender instanceof AbstractEmailSender) {
            AbstractEmailSender aes = (AbstractEmailSender) sender;
            aes.setRetryTimes(retry);
        }
        return async ? sender.sendAsync(req) : sender.send(req);
    }
}
