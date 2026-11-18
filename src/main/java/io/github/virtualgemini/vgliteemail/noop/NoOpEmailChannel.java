package io.github.virtualgemini.vgliteemail.noop;

import io.github.virtualgemini.vgliteemail.api.channel.IEmailChannel;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.common.channel.EmailChannelType;
import io.github.virtualgemini.vgliteemail.common.message.EmailCommonMessages;
import io.github.virtualgemini.vgliteemail.exception.EmailSendException;
import io.github.virtualgemini.vgliteemail.support.util.DisabledEmailResponses;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;

public class NoOpEmailChannel implements IEmailChannel {

    private final LiteEmailLogger logger;

    public NoOpEmailChannel(LiteEmailLogger logger) {
        this.logger = requireLogger(logger);
    }

    @Override
    public String name() {
        return EmailChannelType.NOOP.code();
    }

    @Override
    public SendResponse send(SendRequest request) {
        logger.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        return DisabledEmailResponses.disabled();
    }

    private LiteEmailLogger requireLogger(LiteEmailLogger logger) {
        if (logger == null) {
            throw new EmailSendException(EmailCommonMessages.LOGGER_MUST_NOT_BE_NULL);
        }
        return logger;
    }
}
