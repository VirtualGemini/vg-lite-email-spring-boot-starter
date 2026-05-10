package com.architecturepro.email.core;

import com.architecturepro.email.api.IEmailChannel;
import com.architecturepro.email.channel.impl.SmtpEmailChannel;
import com.architecturepro.email.config.properties.LiteEmailProperties;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailChannel implements IEmailChannel {

    private final IEmailChannel delegate;

    public EmailChannel(LiteEmailProperties properties, JavaMailSender mailSender) {
        delegate = switch (properties.getProtocol()) {
            case SMTP -> new SmtpEmailChannel(mailSender, properties.getSender());
        };
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
    public boolean retryable(int errorCode) {
        return delegate.retryable(errorCode);
    }
}
