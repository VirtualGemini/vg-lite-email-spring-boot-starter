package com.velox.email.spi.channel;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.common.error.EmailErrorCode;

public interface EmailChannel {

    String name();

    SendResponse send(SendRequest request);

    default boolean retryable(SendResponse response) {
        return EmailErrorCode.fromCode(response.errorCode()).retryable();
    }
}
