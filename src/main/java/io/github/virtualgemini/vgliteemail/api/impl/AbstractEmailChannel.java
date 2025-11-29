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

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;

/**
 * Abstract base class for all email channels.
 * Handles common functionality and leaves actual sending to subclasses.
 */
public abstract class AbstractEmailChannel implements IEmailChannel {

    private final String channelName;

    protected AbstractEmailChannel(String channelName) {
        if (channelName == null || channelName.isEmpty()) {
            throw new IllegalArgumentException("Channel name must not be null or empty");
        }
        this.channelName = channelName;
    }

    @Override
    public String name() {
        return channelName;
    }

    @Override
    public abstract SendResponse send(SendRequest request);

    // retryable() uses default from interface; can be overridden by subclass
}


