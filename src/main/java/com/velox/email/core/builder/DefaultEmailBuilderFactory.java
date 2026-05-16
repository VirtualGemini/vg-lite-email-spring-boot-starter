package com.velox.email.core.builder;

import com.velox.email.api.builder.EmailBuilder;
import com.velox.email.api.builder.EmailBuilderFactory;
import com.velox.email.api.sender.EmailSender;
import com.velox.email.api.message.SendRequest;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;

public class DefaultEmailBuilderFactory implements EmailBuilderFactory<EmailBuilder> {

    private final EmailSender sender;
    private final SendRequest defaults;

    public DefaultEmailBuilderFactory(EmailSender sender, SendRequest defaults) {
        this.sender = requireSender(sender);
        this.defaults = requireDefaults(defaults);
    }

    @Override
    public EmailBuilder newMessage() {
        return new EmailBuilder(sender, defaults);
    }

    private EmailSender requireSender(EmailSender sender) {
        if (sender == null) {
            throw new EmailSendException(EmailCommonMessages.SENDER_MUST_NOT_BE_NULL);
        }
        return sender;
    }

    private SendRequest requireDefaults(SendRequest defaults) {
        if (defaults == null) {
            throw new EmailSendException(EmailCommonMessages.DEFAULTS_MUST_NOT_BE_NULL);
        }
        return defaults;
    }
}
