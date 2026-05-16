package com.velox.email.noop;

import com.velox.email.api.channel.IEmailChannel;
import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.error.EmailErrorCode;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.support.util.VeloxEmailLogUtil;

public class NoOpEmailChannel implements IEmailChannel {

    @Override
    public String name() {
        return EmailChannelType.NOOP.code();
    }

    @Override
    public SendResponse send(SendRequest request) {
        VeloxEmailLogUtil.warn(EmailChannelType.NOOP.code(), EmailCommonMessages.EMAIL_CAPABILITY_DISABLED);
        return SendResponse.builder()
                .success(false)
                .error(EmailCommonMessages.EMAIL_CAPABILITY_DISABLED)
                .errorCode(EmailErrorCode.DISABLED)
                .channel(EmailChannelType.NOOP)
                .build();
    }
}
