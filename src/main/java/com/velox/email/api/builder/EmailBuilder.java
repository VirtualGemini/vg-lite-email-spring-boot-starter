package com.velox.email.api.builder;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.sender.EmailSender;
import com.velox.email.spi.builder.AbstractEmailBuilder;

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
