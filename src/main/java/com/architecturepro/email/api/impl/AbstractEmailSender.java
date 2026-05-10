package com.architecturepro.email.api.impl;

import com.architecturepro.email.api.IEmailChannel;
import com.architecturepro.email.api.IEmailSender;
import com.architecturepro.email.config.properties.RetryPolicyProperties;
import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;
import com.architecturepro.email.util.LiteMailLogUtil;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.LockSupport;

public abstract class AbstractEmailSender implements IEmailSender {

    protected final IEmailChannel channel;
    private final Executor executor;
    private final RetryPolicyProperties retryPolicyProperties;

    protected AbstractEmailSender(IEmailChannel channel,
                                  Executor executor,
                                  RetryPolicyProperties retryPolicyProperties) {
        this.channel = channel;
        this.executor = executor;
        this.retryPolicyProperties = retryPolicyProperties;
    }

    @Override
    public SendResponse send(SendRequest request) {
        return send(request, retryPolicyProperties.getGlobalRetries());
    }

    public SendResponse send(SendRequest request, int requestedRetries) {
        LiteMailLogUtil.info(channel.name(), "Start sending email to {}", request.to());

        int configuredRetries = Math.max(1, requestedRetries);
        int finalRetries = Math.min(configuredRetries, Math.max(1, retryPolicyProperties.getMaxRetries()));

        int tried = 0;
        SendResponse response;
        do {
            response = tryOnce(request);
            if (response.success()) {
                LiteMailLogUtil.info(channel.name(), "Email sent successfully on attempt {}", tried + 1);
                return response;
            }
            if (!retryable(response.errorCode()) || !retryPolicyProperties.shouldRetry(tried + 1)) {
                LiteMailLogUtil.warn(channel.name(), "Email failed after {} attempt(s), errorCode={}", tried + 1, response.errorCode());
                return response;
            }
            tried++;
            if (tried >= finalRetries) {
                LiteMailLogUtil.warn(channel.name(), "Retry limit reached after {} attempt(s)", tried);
                return response;
            }
            long delay = retryPolicyProperties.nextDelay(tried);
            LiteMailLogUtil.info(channel.name(), "Retrying after {} ms", delay);
            LockSupport.parkNanos(Duration.ofMillis(delay).toNanos());
        } while (true);
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest request) {
        return CompletableFuture.supplyAsync(() -> send(request), executor);
    }

    public CompletableFuture<SendResponse> sendAsync(SendRequest request, int requestedRetries) {
        return CompletableFuture.supplyAsync(() -> send(request, requestedRetries), executor);
    }

    protected abstract SendResponse tryOnce(SendRequest request);

    protected boolean retryable(int errorCode) {
        return channel.retryable(errorCode);
    }
}
