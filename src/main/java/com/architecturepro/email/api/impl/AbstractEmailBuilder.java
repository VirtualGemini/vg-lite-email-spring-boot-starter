package com.architecturepro.email.api.impl;

import com.architecturepro.email.api.IEmailBuilder;
import com.architecturepro.email.core.EmailAttachment;
import com.architecturepro.email.core.EmailFailureContext;
import com.architecturepro.email.core.EmailMessageBuilder;
import com.architecturepro.email.core.EmailSender;
import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AbstractEmailBuilder<T extends AbstractEmailBuilder<T>> implements IEmailBuilder<T> {

    protected final EmailSender sender;
    protected final SendRequest request;

    protected AbstractEmailBuilder(EmailSender sender) {
        this(sender, SendRequest.builder().build());
    }

    protected AbstractEmailBuilder(EmailSender sender, SendRequest request) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
        this.request = Objects.requireNonNull(request, "request must not be null");
    }

    protected abstract T newBuilder(SendRequest request);

    @Override
    public T to(String... to) {
        return to(Arrays.asList(to));
    }

    @Override
    public T to(Collection<String> to) {
        return with(request.toBuilder().to(to).build());
    }

    @Override
    public T cc(String... cc) {
        return cc(Arrays.asList(cc));
    }

    @Override
    public T cc(Collection<String> cc) {
        return with(request.toBuilder().cc(cc).build());
    }

    @Override
    public T bcc(String... bcc) {
        return bcc(Arrays.asList(bcc));
    }

    @Override
    public T bcc(Collection<String> bcc) {
        return with(request.toBuilder().bcc(bcc).build());
    }

    @Override
    public T replyTo(String replyTo) {
        return with(request.toBuilder().replyTo(replyTo).build());
    }

    @Override
    public T from(String from) {
        return with(request.toBuilder().from(from).build());
    }

    @Override
    public T fromName(String fromName) {
        return with(request.toBuilder().fromName(fromName).build());
    }

    @Override
    public T subject(String subject) {
        return with(request.toBuilder().subject(subject).build());
    }

    @Override
    public T text(String text) {
        return with(request.toBuilder().text(text).build());
    }

    @Override
    public T html(String html) {
        return with(request.toBuilder().html(html).build());
    }

    @Override
    public T html(boolean html) {
        return with(request.toBuilder().textBodyAsHtml(html).build());
    }

    @Override
    public T attachment(Resource resource) {
        return appendAttachment(EmailAttachment.attachment(resource));
    }

    @Override
    public T attachment(File file) {
        return appendAttachment(EmailAttachment.attachment(file));
    }

    @Override
    public T attachment(String filename, byte[] content) {
        return attachment(filename, content, null);
    }

    @Override
    public T attachment(String filename, byte[] content, String contentType) {
        return appendAttachment(EmailAttachment.attachment(filename, content, contentType));
    }

    @Override
    public T attachment(String filename, InputStream inputStream) {
        return attachment(filename, inputStream, null);
    }

    @Override
    public T attachment(String filename, InputStream inputStream, String contentType) {
        return appendAttachment(EmailAttachment.attachment(filename, inputStream, contentType));
    }

    @Override
    public T attachment(String filename, InputStreamSource source, String contentType) {
        return appendAttachment(EmailAttachment.attachment(filename, source, contentType));
    }

    @Override
    public T inline(String contentId, String filename, byte[] content, String contentType) {
        return appendInline(EmailAttachment.inline(contentId, filename, content, contentType));
    }

    @Override
    public T inline(String contentId, String filename, InputStream inputStream, String contentType) {
        return appendInline(EmailAttachment.inline(contentId, filename, inputStream, contentType));
    }

    @Override
    public T inline(String contentId, String filename, InputStreamSource source, String contentType) {
        return appendInline(EmailAttachment.inline(contentId, filename, source, contentType));
    }

    @Override
    public T retry(int maxAttempts) {
        return with(request.toBuilder().maxAttempts(maxAttempts).build());
    }

    @Override
    public T retry() {
        return retry(2);
    }

    @Override
    public T async() {
        return with(request.toBuilder().async(true).build());
    }

    @Override
    public T onFailure(Consumer<EmailFailureContext> failureHook) {
        return with(request.toBuilder().failureHook(failureHook).build());
    }

    @Override
    public SendRequest build() {
        return request;
    }

    @Override
    public SendResponse sendSync() {
        return sender.send(request.toBuilder().async(false).build());
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync() {
        return sender.sendAsync(request.toBuilder().async(true).build());
    }

    @Override
    public Object send() {
        return request.async() ? sendAsync() : sendSync();
    }

    private T appendAttachment(EmailAttachment attachment) {
        List<EmailAttachment> attachments = new ArrayList<>(request.attachments());
        attachments.add(attachment);
        return with(request.toBuilder().attachments(attachments).build());
    }

    private T appendInline(EmailAttachment attachment) {
        List<EmailAttachment> inlineResources = new ArrayList<>(request.inlineResources());
        inlineResources.add(attachment);
        return with(request.toBuilder().inlineResources(inlineResources).build());
    }

    private T with(SendRequest request) {
        return newBuilder(request);
    }
}
