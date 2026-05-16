package com.velox.email.core.sender;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.spi.channel.EmailChannel;
import com.velox.email.spi.policy.EmailExceptionTranslator;
import com.velox.email.spi.hook.EmailSendInterceptor;
import com.velox.email.spi.hook.EmailSendListener;
import com.velox.email.spi.policy.RetryPolicy;
import com.velox.email.spi.sender.AbstractEmailSender;

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
