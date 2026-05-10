package com.architecturepro.email.api.impl;

import com.architecturepro.email.api.IEmailBuilder;
import com.architecturepro.email.api.IEmailSender;
import com.architecturepro.email.core.SendRequest;

public abstract class AbstractEmailBuilder<T extends AbstractEmailBuilder<T>> implements IEmailBuilder<T> {

    protected final String to;
    protected final String subject;
    protected final String text;
    protected final boolean html;
    protected final int retry;
    protected final boolean async;

    protected AbstractEmailBuilder() {
        this(null, null, null, false, 1, false);
    }

    protected AbstractEmailBuilder(String to, String subject, String text, boolean html, int retry, boolean async) {
        this.to = to;
        this.subject = subject;
        this.text = text;
        this.html = html;
        this.retry = retry;
        this.async = async;
    }

    protected abstract IEmailSender sender();

    protected abstract T newBuilder(String to, String subject, String text, boolean html, int retry, boolean async);

    @Override
    public T to(String to) {
        return newBuilder(to, subject, text, html, retry, async);
    }

    @Override
    public T subject(String subject) {
        return newBuilder(to, subject, text, html, retry, async);
    }

    @Override
    public T text(String text) {
        return newBuilder(to, subject, text, html, retry, async);
    }

    @Override
    public T html(boolean html) {
        return newBuilder(to, subject, text, html, retry, async);
    }

    @Override
    public T retry(int times) {
        return newBuilder(to, subject, text, html, Math.max(1, times), async);
    }

    @Override
    public T retry() {
        return retry(2);
    }

    @Override
    public T async() {
        return newBuilder(to, subject, text, html, retry, true);
    }

    @Override
    public Object send() {
        SendRequest request = SendRequest.builder()
                .to(to)
                .subject(subject)
                .text(text)
                .html(html)
                .build();

        IEmailSender emailSender = sender();
        if (emailSender instanceof AbstractEmailSender abstractSender) {
            return async ? abstractSender.sendAsync(request, retry) : abstractSender.send(request, retry);
        }
        return async ? emailSender.sendAsync(request) : emailSender.send(request);
    }
}
