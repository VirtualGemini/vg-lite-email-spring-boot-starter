package com.velox.email.core;

public interface EmailSendListener {

    default void onSuccess(SendRequest request, SendResponse response) {
    }

    default void onFailure(SendRequest request, SendResponse response, Throwable cause) {
    }
}
