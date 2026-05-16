package com.architecturepro.email.core;

import java.util.Objects;

public class DefaultEmailBuilderFactory implements EmailBuilderFactory<EmailBuilder> {

    private final EmailSender sender;
    private final SendRequest defaults;

    public DefaultEmailBuilderFactory(EmailSender sender, SendRequest defaults) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.defaults = Objects.requireNonNull(defaults, "defaults must not be null");
    }

    @Override
    public EmailBuilder newMessage() {
        return new EmailBuilder(sender, defaults);
    }
}
