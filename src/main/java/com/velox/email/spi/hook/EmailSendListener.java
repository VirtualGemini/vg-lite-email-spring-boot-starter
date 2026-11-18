package com.velox.email.spi.hook;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;

public interface EmailSendListener {

    default void onSuccess(SendRequest request, SendResponse response) {
    }

    default void onFailure(SendRequest request, SendResponse response, Throwable cause) {
    }
}
