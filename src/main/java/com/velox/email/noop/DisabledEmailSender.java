package com.velox.email.noop;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.api.sender.IEmailSender;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;
import com.velox.email.support.util.DisabledEmailResponses;
import com.velox.email.support.util.VeloxEmailLogger;

import java.util.concurrent.CompletableFuture;

public class DisabledEmailSender implements IEmailSender {

    private final VeloxEmailLogger logger;

    public DisabledEmailSender(VeloxEmailLogger logger) {
        this.logger = requireLogger(logger);
    }

    @Override
    public SendResponse send(SendRequest request) {
        logger.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        return DisabledEmailResponses.disabled();
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest request) {
        logger.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        return CompletableFuture.completedFuture(DisabledEmailResponses.disabled());
    }

    private VeloxEmailLogger requireLogger(VeloxEmailLogger logger) {
        if (logger == null) {
            throw new EmailSendException(EmailCommonMessages.LOGGER_MUST_NOT_BE_NULL);
        }
        return logger;
    }
}
