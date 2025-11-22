/*
 * Copyright 2025 Your VirtualGemini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.virtualgemini.vgliteemail.core;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: 发送请求 / Send request
 * @createDate 2025/11/21 21:46
 */

/* ---------- SendRequest ---------- */
public class SendRequest {
    private String to;
    private String subject;
    private String text;
    private boolean html;

    private SendRequest(Builder b) {
        this.to = b.to;
        this.subject = b.subject;
        this.text = b.text;
        this.html = b.html;
    }

    /* ---------- getter ---------- */
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getText() { return text; }
    public boolean isHtml() { return html; }

    /* ---------- Builder ---------- */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String to;
        private String subject;
        private String text;
        private boolean html;

        public Builder to(String to) { this.to = to; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder text(String text) { this.text = text; return this; }
        public Builder html(boolean html) { this.html = html; return this; }
        public SendRequest build() { return new SendRequest(this); }
    }
}
