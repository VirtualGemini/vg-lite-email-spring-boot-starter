package com.architecturepro.email.core;

public interface EmailExceptionTranslator {

    SendResponse translate(String channelName, SendRequest request, Throwable throwable);
}
