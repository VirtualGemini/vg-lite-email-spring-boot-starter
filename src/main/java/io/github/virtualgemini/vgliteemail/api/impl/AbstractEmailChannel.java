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


