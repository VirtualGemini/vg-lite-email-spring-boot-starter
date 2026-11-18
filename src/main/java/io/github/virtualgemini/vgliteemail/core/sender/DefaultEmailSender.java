package io.github.virtualgemini.vgliteemail.core.sender;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.spi.channel.EmailChannel;
import io.github.virtualgemini.vgliteemail.spi.policy.EmailExceptionTranslator;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendInterceptor;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendListener;
import io.github.virtualgemini.vgliteemail.spi.policy.RetryPolicy;
import io.github.virtualgemini.vgliteemail.spi.sender.AbstractEmailSender;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;

import java.util.List;
import java.util.concurrent.Executor;

public class DefaultEmailSender extends AbstractEmailSender {

    public DefaultEmailSender(EmailChannel channel,
                              LiteEmailLogger logger,
                              Executor executor,
                              RetryPolicy retryPolicy,
                              EmailExceptionTranslator exceptionTranslator,
                              List<EmailSendInterceptor> interceptors,
                              List<EmailSendListener> listeners,
                              SendRequest defaults) {
        super(channel, logger, executor, retryPolicy, exceptionTranslator, interceptors, listeners, defaults);
    }

    @Override
    protected SendResponse doSend(SendRequest request) {
        return channel.send(request);
    }
}
