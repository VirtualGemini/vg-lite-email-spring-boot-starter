package com.architecturepro.email.core;

import com.architecturepro.email.api.impl.AbstractEmailBuilder;
import com.architecturepro.email.api.IEmailBuilder;
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
