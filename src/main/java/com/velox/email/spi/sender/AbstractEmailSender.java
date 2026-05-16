package com.velox.email.spi.sender;

import com.velox.email.api.sender.IEmailSender;
import com.velox.email.spi.channel.EmailChannel;
import com.velox.email.spi.policy.EmailExceptionTranslator;
import com.velox.email.api.message.EmailFailureContext;
import com.velox.email.spi.hook.EmailSendInterceptor;
import com.velox.email.spi.hook.EmailSendListener;
import com.velox.email.spi.policy.RetryPolicy;
import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailSendException;
import com.velox.email.support.util.VeloxEmailLogUtil;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractEmailSender implements IEmailSender {

    protected final EmailChannel channel;
    private final Executor executor;
    private final RetryPolicy retryPolicy;
    private final EmailExceptionTranslator exceptionTranslator;
    private final List<EmailSendInterceptor> interceptors;
    private final List<EmailSendListener> listeners;
    private final SendRequest defaults;

    protected AbstractEmailSender(EmailChannel channel,
                                  Executor executor,
                                  RetryPolicy retryPolicy,
                                  EmailExceptionTranslator exceptionTranslator,
                                  List<EmailSendInterceptor> interceptors,
                                  List<EmailSendListener> listeners,
                                  SendRequest defaults) {
        this.channel = requireChannel(channel);
        this.executor = requireExecutor(executor);
        this.retryPolicy = requireRetryPolicy(retryPolicy);
        this.exceptionTranslator = requireExceptionTranslator(exceptionTranslator);
        this.interceptors = List.copyOf(interceptors);
        this.listeners = List.copyOf(listeners);
        this.defaults = requireDefaults(defaults);
    }

    @Override
    public SendResponse send(SendRequest request) {
        SendRequest mergedRequest = applyInterceptors(applyDefaults(request));
        validateRequest(mergedRequest);
        VeloxEmailLogUtil.info(channel.name(), "Start sending email to {}", mergedRequest.to());

        RetryPolicy activeRetryPolicy = mergedRequest.retryPolicy() != null ? mergedRequest.retryPolicy() : retryPolicy;
        SendResponse finalResponse = null;
        Throwable finalCause = null;

        for (int attempt = 1; ; attempt++) {
            try {
                finalResponse = enrichResponse(doSend(mergedRequest), attempt);
                finalCause = null;
            } catch (Throwable throwable) {
                finalCause = throwable;
                finalResponse = enrichResponse(exceptionTranslator.translate(channel.name(), mergedRequest, throwable), attempt);
            }

            if (finalResponse.success()) {
                SendResponse response = applyAfterInterceptors(mergedRequest, finalResponse);
                VeloxEmailLogUtil.info(channel.name(), "Email sent successfully on attempt {}", attempt);
                notifySuccess(mergedRequest, response);
                return response;
            }

            if (!channel.retryable(finalResponse) || !activeRetryPolicy.shouldRetry(mergedRequest, finalResponse, attempt)) {
                SendResponse response = applyAfterInterceptors(mergedRequest, finalResponse);
                VeloxEmailLogUtil.warn(channel.name(), "Email failed after {} attempt(s), errorCode={}", attempt, response.errorCode());
                notifyFailure(mergedRequest, response, finalCause);
                return response;
            }

            Duration delay = activeRetryPolicy.nextDelay(mergedRequest, finalResponse, attempt);
            VeloxEmailLogUtil.info(channel.name(), "Retrying after {} ms", delay.toMillis());
            LockSupport.parkNanos(delay.toNanos());
        }
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

    protected abstract SendResponse doSend(SendRequest request);

    protected SendRequest defaults() {
        return defaults;
    }

    private SendRequest applyDefaults(SendRequest request) {
        if (request == null) {
            return defaults;
        }
        SendRequest.Builder builder = request.toBuilder();
        if (isBlank(request.from())) {
            builder.from(defaults.from());
        }
        if (isBlank(request.fromName())) {
            builder.fromName(defaults.fromName());
        }
        if (isBlank(request.replyTo())) {
            builder.replyTo(defaults.replyTo());
        }
        return builder.build();
    }

    private SendRequest applyInterceptors(SendRequest request) {
        SendRequest current = request;
        for (EmailSendInterceptor interceptor : interceptors) {
            current = requireBeforeInterceptorResult(interceptor.beforeSend(current));
        }
        return current;
    }

    private SendResponse applyAfterInterceptors(SendRequest request, SendResponse response) {
        SendResponse current = response;
        for (EmailSendInterceptor interceptor : interceptors) {
            current = requireAfterInterceptorResult(interceptor.afterSend(request, current));
        }
        return current;
    }

    private SendResponse enrichResponse(SendResponse response, int attempt) {
        return SendResponse.builder()
                .from(response)
                .attempts(attempt)
                .channel(channel.name())
                .build();
    }

    private void validateRequest(SendRequest request) {
        if (!request.hasRecipients()) {
            throw new EmailSendException(EmailCommonMessages.EMAIL_RECIPIENTS_MUST_NOT_BE_EMPTY);
        }
        if (isBlank(request.from())) {
            throw new EmailSendException(EmailCommonMessages.EMAIL_FROM_ADDRESS_MUST_NOT_BE_BLANK);
        }
    }

    private void notifySuccess(SendRequest request, SendResponse response) {
        for (EmailSendListener listener : listeners) {
            listener.onSuccess(request, response);
        }
    }

    private void notifyFailure(SendRequest request, SendResponse response, Throwable cause) {
        for (EmailSendListener listener : listeners) {
            listener.onFailure(request, response, cause);
        }
        if (request.failureHook() != null) {
            request.failureHook().accept(new EmailFailureContext(request, response, cause));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private EmailChannel requireChannel(EmailChannel channel) {
        if (channel == null) {
            throw new EmailSendException(EmailCommonMessages.CHANNEL_MUST_NOT_BE_NULL);
        }
        return channel;
    }

    private Executor requireExecutor(Executor executor) {
        if (executor == null) {
            throw new EmailSendException(EmailCommonMessages.EXECUTOR_MUST_NOT_BE_NULL);
        }
        return executor;
    }

    private RetryPolicy requireRetryPolicy(RetryPolicy retryPolicy) {
        if (retryPolicy == null) {
            throw new EmailSendException(EmailCommonMessages.RETRY_POLICY_MUST_NOT_BE_NULL);
        }
        return retryPolicy;
    }

    private EmailExceptionTranslator requireExceptionTranslator(EmailExceptionTranslator exceptionTranslator) {
        if (exceptionTranslator == null) {
            throw new EmailSendException(EmailCommonMessages.EXCEPTION_TRANSLATOR_MUST_NOT_BE_NULL);
        }
        return exceptionTranslator;
    }

    private SendRequest requireDefaults(SendRequest defaults) {
        if (defaults == null) {
            throw new EmailSendException(EmailCommonMessages.DEFAULTS_MUST_NOT_BE_NULL);
        }
        return defaults;
    }

    private SendRequest requireBeforeInterceptorResult(SendRequest request) {
        if (request == null) {
            throw new EmailSendException(EmailCommonMessages.EMAIL_SEND_INTERCEPTOR_BEFORE_RETURNED_NULL);
        }
        return request;
    }

    private SendResponse requireAfterInterceptorResult(SendResponse response) {
        if (response == null) {
            throw new EmailSendException(EmailCommonMessages.EMAIL_SEND_INTERCEPTOR_AFTER_RETURNED_NULL);
        }
        return response;
    }
}
