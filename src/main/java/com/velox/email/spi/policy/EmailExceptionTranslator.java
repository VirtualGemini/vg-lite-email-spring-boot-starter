package com.velox.email.spi.policy;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;

public interface EmailExceptionTranslator {

    SendResponse translate(String channelName, SendRequest request, Throwable throwable);
}
