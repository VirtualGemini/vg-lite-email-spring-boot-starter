package com.architecturepro.email.core;

public interface EmailSendInterceptor {

    default SendRequest beforeSend(SendRequest request) {
        return request;
    }

    default SendResponse afterSend(SendRequest request, SendResponse response) {
        return response;
    }
}
