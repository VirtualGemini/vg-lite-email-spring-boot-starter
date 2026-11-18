package io.github.virtualgemini.vgliteemail.spi.policy;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

public interface EmailExceptionTranslator {

    SendResponse translate(String channelName, SendRequest request, Throwable throwable);
}
