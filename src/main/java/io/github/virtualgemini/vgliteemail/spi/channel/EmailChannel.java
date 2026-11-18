package io.github.virtualgemini.vgliteemail.spi.channel;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.common.error.EmailErrorCode;

public interface EmailChannel {

    String name();

    SendResponse send(SendRequest request);

    default boolean retryable(SendResponse response) {
        return EmailErrorCode.fromCode(response.errorCode()).retryable();
    }
}
