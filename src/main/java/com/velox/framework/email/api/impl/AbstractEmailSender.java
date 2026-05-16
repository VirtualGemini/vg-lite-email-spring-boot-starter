package com.velox.email.api.impl;

import com.velox.email.api.IEmailSender;
import com.velox.email.core.EmailChannel;
import com.velox.email.core.EmailExceptionTranslator;
import com.velox.email.core.EmailFailureContext;
import com.velox.email.core.EmailSendInterceptor;
import com.velox.email.core.EmailSendListener;
import com.velox.email.core.RetryPolicy;
import com.velox.email.core.SendRequest;
import com.velox.email.core.SendResponse;
import com.velox.email.util.VeloxEmailLogUtil;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
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
        this.channel = Objects.requireNonNull(channel, "channel must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
        this.exceptionTranslator = Objects.requireNonNull(exceptionTranslator, "exceptionTranslator must not be null");
        this.interceptors = List.copyOf(interceptors);
        this.listeners = List.copyOf(listeners);
        this.defaults = Objects.requireNonNull(defaults, "defaults must not be null");
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
            current = Objects.requireNonNull(interceptor.beforeSend(current), "EmailSendInterceptor.beforeSend must not return null");
        }
        return current;
    }

    private SendResponse applyAfterInterceptors(SendRequest request, SendResponse response) {
        SendResponse current = response;
        for (EmailSendInterceptor interceptor : interceptors) {
            current = Objects.requireNonNull(interceptor.afterSend(request, current), "EmailSendInterceptor.afterSend must not return null");
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
            throw new IllegalArgumentException("Email recipients must not be empty");
        }
        if (isBlank(request.from())) {
            throw new IllegalArgumentException("Email from address must not be blank");
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
}
