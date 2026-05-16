package com.velox.email.core;

import com.velox.email.api.impl.AbstractEmailBuilder;
import com.velox.email.api.IEmailBuilder;
public class EmailBuilder extends AbstractEmailBuilder<EmailBuilder> implements IEmailBuilder<EmailBuilder> {

    public EmailBuilder(EmailSender sender) {
        this(sender, SendRequest.builder().build());
    }

    public EmailBuilder(EmailSender sender, SendRequest request) {
        super(sender, request);
    }

    @Override
    protected EmailBuilder newBuilder(SendRequest request) {
        return new EmailBuilder(sender, request);
    }
}
