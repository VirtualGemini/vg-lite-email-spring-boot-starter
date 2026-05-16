package com.architecturepro.email.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public final class SendRequest {

    private final String from;
    private final String fromName;
    private final String replyTo;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String text;
    private final String html;
    private final boolean textBodyAsHtml;
    private final List<EmailAttachment> attachments;
    private final List<EmailAttachment> inlineResources;
    private final int maxAttempts;
    private final boolean async;
    private final RetryPolicy retryPolicy;
    private final Consumer<EmailFailureContext> failureHook;

    private SendRequest(Builder builder) {
        this.from = builder.from;
        this.fromName = builder.fromName;
        this.replyTo = builder.replyTo;
        this.to = List.copyOf(builder.to);
        this.cc = List.copyOf(builder.cc);
        this.bcc = List.copyOf(builder.bcc);
        this.subject = builder.subject;
        this.text = builder.text;
        this.html = builder.html;
        this.textBodyAsHtml = builder.textBodyAsHtml;
        this.attachments = List.copyOf(builder.attachments);
        this.inlineResources = List.copyOf(builder.inlineResources);
        this.maxAttempts = builder.maxAttempts;
        this.async = builder.async;
        this.retryPolicy = builder.retryPolicy;
        this.failureHook = builder.failureHook;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public String from() {
        return from;
    }

    public String fromName() {
        return fromName;
    }

    public String replyTo() {
        return replyTo;
    }

    public List<String> to() {
        return to;
    }

    public List<String> cc() {
        return cc;
    }

    public List<String> bcc() {
        return bcc;
    }

    public String subject() {
        return subject;
    }

    public String text() {
        return text;
    }

    public String html() {
        return html;
    }

    public boolean textBodyAsHtml() {
        return textBodyAsHtml;
    }

    public List<EmailAttachment> attachments() {
        return attachments;
    }

    public List<EmailAttachment> inlineResources() {
        return inlineResources;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public boolean async() {
        return async;
    }

    public RetryPolicy retryPolicy() {
        return retryPolicy;
    }

    public Consumer<EmailFailureContext> failureHook() {
        return failureHook;
    }

    public String resolveHtmlBody() {
        if (html != null && !html.isBlank()) {
            return html;
        }
        if (textBodyAsHtml) {
            return text;
        }
        return null;
    }

    public String resolveTextBody() {
        if (textBodyAsHtml && (html == null || html.isBlank())) {
            return null;
        }
        return text;
    }

    public boolean hasAttachments() {
        return !attachments.isEmpty();
    }

    public boolean hasInlineResources() {
        return !inlineResources.isEmpty();
    }

    public boolean hasHtmlBody() {
        String htmlBody = resolveHtmlBody();
        return htmlBody != null && !htmlBody.isBlank();
    }

    public boolean hasRecipients() {
        return !to.isEmpty() || !cc.isEmpty() || !bcc.isEmpty();
    }

    public static final class Builder {
        private String from;
        private String fromName;
        private String replyTo;
        private List<String> to = List.of();
        private List<String> cc = List.of();
        private List<String> bcc = List.of();
        private String subject;
        private String text;
        private String html;
        private boolean textBodyAsHtml;
        private List<EmailAttachment> attachments = List.of();
        private List<EmailAttachment> inlineResources = List.of();
        private int maxAttempts;
        private boolean async;
        private RetryPolicy retryPolicy;
        private Consumer<EmailFailureContext> failureHook;

        private Builder() {
        }

        private Builder(SendRequest source) {
            this.from = source.from;
            this.fromName = source.fromName;
            this.replyTo = source.replyTo;
            this.to = source.to;
            this.cc = source.cc;
            this.bcc = source.bcc;
            this.subject = source.subject;
            this.text = source.text;
            this.html = source.html;
            this.textBodyAsHtml = source.textBodyAsHtml;
            this.attachments = source.attachments;
            this.inlineResources = source.inlineResources;
            this.maxAttempts = source.maxAttempts;
            this.async = source.async;
            this.retryPolicy = source.retryPolicy;
            this.failureHook = source.failureHook;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public Builder to(Collection<String> to) {
            this.to = normalizeEmails(to);
            return this;
        }

        public Builder cc(Collection<String> cc) {
            this.cc = normalizeEmails(cc);
            return this;
        }

        public Builder bcc(Collection<String> bcc) {
            this.bcc = normalizeEmails(bcc);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder html(String html) {
            this.html = html;
            return this;
        }

        public Builder textBodyAsHtml(boolean textBodyAsHtml) {
            this.textBodyAsHtml = textBodyAsHtml;
            return this;
        }

        public Builder attachments(Collection<EmailAttachment> attachments) {
            this.attachments = List.copyOf(attachments);
            return this;
        }

        public Builder inlineResources(Collection<EmailAttachment> inlineResources) {
            this.inlineResources = List.copyOf(inlineResources);
            return this;
        }

        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = Math.max(1, maxAttempts);
            return this;
        }

        public Builder async(boolean async) {
            this.async = async;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
            return this;
        }

        public Builder failureHook(Consumer<EmailFailureContext> failureHook) {
            this.failureHook = failureHook;
            return this;
        }

        public SendRequest build() {
            return new SendRequest(this);
        }

        private static List<String> normalizeEmails(Collection<String> emails) {
            if (emails == null || emails.isEmpty()) {
                return List.of();
            }
            List<String> normalized = new ArrayList<>(emails.size());
            for (String email : emails) {
                if (email == null) {
                    continue;
                }
                String trimmed = email.trim();
                if (!trimmed.isEmpty()) {
                    normalized.add(trimmed);
                }
            }
            return List.copyOf(normalized);
        }
    }
}
