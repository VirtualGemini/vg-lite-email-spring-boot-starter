package io.github.virtualgemini.vgliteemail.core.builder;

import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilderFactory;
import io.github.virtualgemini.vgliteemail.api.sender.EmailSender;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.common.message.EmailCommonMessages;
import io.github.virtualgemini.vgliteemail.exception.EmailSendException;

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
