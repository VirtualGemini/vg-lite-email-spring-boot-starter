package com.velox.email.support.util;

import com.velox.email.api.message.SendResponse;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.error.EmailErrorCode;
import com.velox.email.common.message.EmailCommonMessages;

public final class DisabledEmailResponses {

    private DisabledEmailResponses() {
    }

    public static SendResponse disabled() {
        return SendResponse.builder()
                .success(false)
                .error(EmailCommonMessages.EMAIL_CAPABILITY_DISABLED)
                .errorCode(EmailErrorCode.DISABLED)
                .channel(EmailChannelType.NOOP)
                .build();
    }
}
