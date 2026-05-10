package com.architecturepro.email.core;

public record SendRequest(
        String to,
        String subject,
        String text,
        boolean html
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String to;
        private String subject;
        private String text;
        private boolean html;

        public Builder to(String to) {
            this.to = to;
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

        public Builder html(boolean html) {
            this.html = html;
            return this;
        }

        public SendRequest build() {
            return new SendRequest(to, subject, text, html);
        }
    }
}
