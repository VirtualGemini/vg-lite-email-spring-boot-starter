package com.velox.email.core;

import com.velox.email.api.impl.AbstractEmailSender;

import java.util.List;
import java.util.concurrent.Executor;

public class DefaultEmailSender extends AbstractEmailSender {

    public DefaultEmailSender(EmailChannel channel,
                              Executor executor,
                              RetryPolicy retryPolicy,
                              EmailExceptionTranslator exceptionTranslator,
                              List<EmailSendInterceptor> interceptors,
                              List<EmailSendListener> listeners,
                              SendRequest defaults) {
        super(channel, executor, retryPolicy, exceptionTranslator, interceptors, listeners, defaults);
    }

    @Override
    protected SendResponse doSend(SendRequest request) {
        return channel.send(request);
    }
}
