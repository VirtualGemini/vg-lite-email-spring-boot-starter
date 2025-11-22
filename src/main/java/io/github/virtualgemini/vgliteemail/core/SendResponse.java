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
 * @description: 发送响应 / Send response
 * @createDate 2025/11/21 21:46
 */

/**
 * 发送响应 -- Send response
 */
public class SendResponse {
    private boolean success;
    private String msgId;
    private String error;
    private int errorCode;

    /* ---------- 构造器 ---------- */
    public SendResponse() { }

    /* ---------- Builder ---------- */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private boolean success;
        private String msgId;
        private String error;
        private int errorCode;

        public Builder success(boolean success) { this.success = success; return this; }
        public Builder msgId(String msgId) { this.msgId = msgId; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder errorCode(int errorCode) { this.errorCode = errorCode; return this; }
        public SendResponse build() {
            SendResponse res = new SendResponse();
            res.success = this.success;
            res.msgId = this.msgId;
            res.error = this.error;
            res.errorCode = this.errorCode;
            return res;
        }
    }

    /* ---------- getter ---------- */
    public boolean isSuccess() { return success; }
    public String getMsgId() { return msgId; }
    public String getError() { return error; }
    public int getErrorCode() { return errorCode; }
}
