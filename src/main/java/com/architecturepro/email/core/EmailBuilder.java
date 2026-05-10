package com.architecturepro.email.core;

import com.architecturepro.email.api.IEmailSender;
import com.architecturepro.email.api.impl.AbstractEmailBuilder;

public class EmailBuilder extends AbstractEmailBuilder<EmailBuilder> {

    private final IEmailSender sender;

    public EmailBuilder(IEmailSender sender) {
        this(sender, null, null, null, false, 1, false);
    }

    private EmailBuilder(IEmailSender sender, String to, String subject, String text, boolean html, int retry, boolean async) {
        super(to, subject, text, html, retry, async);
        this.sender = sender;
    }

    @Override
    protected IEmailSender sender() {
        return sender;
    }

    @Override
    protected EmailBuilder newBuilder(String to, String subject, String text, boolean html, int retry, boolean async) {
        return new EmailBuilder(sender, to, subject, text, html, retry, async);
    }
}
