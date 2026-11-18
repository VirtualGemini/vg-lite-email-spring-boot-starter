package io.github.virtualgemini.vgliteemail.spi.hook;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

public interface EmailSendInterceptor {

    default SendRequest beforeSend(SendRequest request) {
        return request;
    }

    default SendResponse afterSend(SendRequest request, SendResponse response) {
        return response;
    }
}
