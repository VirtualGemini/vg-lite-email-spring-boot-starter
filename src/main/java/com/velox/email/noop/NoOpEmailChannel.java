package com.velox.email.noop;

import com.velox.email.api.channel.IEmailChannel;
import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;
import com.velox.email.support.util.DisabledEmailResponses;
import com.velox.email.support.util.VeloxEmailLogger;

public class NoOpEmailChannel implements IEmailChannel {

    private final VeloxEmailLogger logger;

    public NoOpEmailChannel(VeloxEmailLogger logger) {
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

    private VeloxEmailLogger requireLogger(VeloxEmailLogger logger) {
        if (logger == null) {
            throw new EmailSendException(EmailCommonMessages.LOGGER_MUST_NOT_BE_NULL);
        }
        return logger;
    }
}
