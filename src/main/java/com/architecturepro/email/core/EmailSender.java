package com.architecturepro.email.core;

import com.architecturepro.email.api.IEmailChannel;
import com.architecturepro.email.api.impl.AbstractEmailSender;
import com.architecturepro.email.config.properties.RetryPolicyProperties;

import java.util.concurrent.Executor;

public class EmailSender extends AbstractEmailSender {

    public EmailSender(IEmailChannel channel, Executor executor, RetryPolicyProperties retryPolicyProperties) {
        super(channel, executor, retryPolicyProperties);
    }

    @Override
    protected SendResponse tryOnce(SendRequest request) {
        return channel.send(request);
    }
}
