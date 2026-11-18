package io.github.virtualgemini.vgliteemail.support.util;

import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.common.channel.EmailChannelType;
import io.github.virtualgemini.vgliteemail.common.error.EmailErrorCode;
import io.github.virtualgemini.vgliteemail.common.message.EmailCommonMessages;

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
