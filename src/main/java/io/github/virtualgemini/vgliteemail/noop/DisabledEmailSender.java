package io.github.virtualgemini.vgliteemail.noop;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.api.sender.IEmailSender;
import io.github.virtualgemini.vgliteemail.common.channel.EmailChannelType;
import io.github.virtualgemini.vgliteemail.common.message.EmailCommonMessages;
import io.github.virtualgemini.vgliteemail.exception.EmailSendException;
import io.github.virtualgemini.vgliteemail.support.util.DisabledEmailResponses;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;

import java.util.concurrent.CompletableFuture;

public class DisabledEmailSender implements IEmailSender {

    private final LiteEmailLogger logger;

    public DisabledEmailSender(LiteEmailLogger logger) {
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

    private LiteEmailLogger requireLogger(LiteEmailLogger logger) {
        if (logger == null) {
            throw new EmailSendException(EmailCommonMessages.LOGGER_MUST_NOT_BE_NULL);
        }
        return logger;
    }
}
