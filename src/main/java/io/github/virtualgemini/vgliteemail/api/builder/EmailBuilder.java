package io.github.virtualgemini.vgliteemail.api.builder;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.sender.EmailSender;
import io.github.virtualgemini.vgliteemail.spi.builder.AbstractEmailBuilder;

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
