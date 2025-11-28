package io.github.virtualgemini.vgliteemail.api.impl;

import io.github.virtualgemini.vgliteemail.api.IEmailBuilder;
import io.github.virtualgemini.vgliteemail.api.IEmailSender;
import io.github.virtualgemini.vgliteemail.core.SendRequest;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:13
 */
public abstract class AbstractEmailBuilder<T extends AbstractEmailBuilder<T>>
        implements IEmailBuilder<T> {

    protected String to;
    protected String subject;
    protected String text;
    protected boolean html;
    protected int retry = 1;
    protected boolean async;

    protected abstract IEmailSender sender();

    @SuppressWarnings("unchecked")
    protected T self() { return (T) this; }

    @Override
    public T to(String to) { this.to = to; return self(); }
    @Override
    public T subject(String subject) { this.subject = subject; return self(); }
    @Override
    public T text(String text) { this.text = text; return self(); }
    @Override
    public T html(boolean html) { this.html = html; return self(); }
    @Override
    public T retry(int times) { this.retry = times; return self(); }
    @Override
    public T retry() { return self(); }
    @Override
    public T async() { this.async = true; return self(); }

    @Override
    public Object send() {
        SendRequest req = SendRequest.builder()
                .to(to).subject(subject).text(text).html(html).build();
        IEmailSender sender = sender();
        if (sender instanceof AbstractEmailSender) {
            AbstractEmailSender aes = (AbstractEmailSender) sender;
            aes.setRetryTimes(retry);
        }
        return async ? sender.sendAsync(req) : sender.send(req);
    }
}
