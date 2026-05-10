package com.architecturepro.email.core;

public record SendResponse(
        boolean success,
        String msgId,
        String error,
        int errorCode
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean success;
        private String msgId;
        private String error;
        private int errorCode;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder msgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder errorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public SendResponse build() {
            return new SendResponse(success, msgId, error, errorCode);
        }
    }
}
