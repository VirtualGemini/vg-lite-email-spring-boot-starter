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
import io.github.virtualgemini.vgliteemail.channel.impl.SmtpEmailChannel;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Single entry channel for all protocols.
 */
public class EmailChannel implements IEmailChannel {

    private final IEmailChannel delegate;

    public EmailChannel(LiteEmailProperties properties, JavaMailSender mailSender) {
        String protocol = properties.getProtocol().toUpperCase();

        switch (protocol) {
            case "SMTP":
                delegate = new SmtpEmailChannel(mailSender, properties.getSender());
                break;
            // case "API": // future support
            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public SendResponse send(SendRequest request) {
        return delegate.send(request);
    }

    @Override
    public boolean retryable(int code) {
        return delegate.retryable(code);
    }
}

