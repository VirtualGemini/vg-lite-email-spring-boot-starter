package com.velox.email.spi.hook;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;

public interface EmailSendInterceptor {

    default SendRequest beforeSend(SendRequest request) {
        return request;
    }

    default SendResponse afterSend(SendRequest request, SendResponse response) {
        return response;
    }
}
