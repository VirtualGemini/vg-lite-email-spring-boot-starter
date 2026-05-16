package com.velox.email.noop;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.api.sender.IEmailSender;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;
import com.velox.email.support.util.VeloxEmailLogUtil;

import java.util.concurrent.CompletableFuture;

public class DisabledEmailSender implements IEmailSender {

    @Override
    public SendResponse send(SendRequest request) {
        VeloxEmailLogUtil.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        throw new EmailSendException(EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest request) {
        VeloxEmailLogUtil.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        return CompletableFuture.failedFuture(new EmailSendException(EmailCommonMessages.EMAIL_CAPABILITY_DISABLED));
    }
}
