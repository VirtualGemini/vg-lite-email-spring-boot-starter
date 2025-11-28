package io.github.virtualgemini.vgliteemail.core;

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.channel.impl.SmtpEmailChannel;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Single entry channel for all protocols.
 */
public class EmailChannel implements IEmailChannel {

    private final IEmailChannel delegate;

    public EmailChannel(LiteEmailProperties properties, JavaMailSender mailSender) {
        String protocol = properties.getProtocol().toUpperCase();

        switch (protocol) {
            case "SMTP":
                delegate = new SmtpEmailChannel(mailSender, properties.getSender());
                break;
            // case "API": // future support
            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
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
    public boolean retryable(int code) {
        return delegate.retryable(code);
    }
}

