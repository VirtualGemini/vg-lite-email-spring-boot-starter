package com.velox.email.api.message;

import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.error.EmailErrorCode;

public record SendResponse(
        boolean success,
        String msgId,
        String error,
        int errorCode,
        int attempts,
        String channel
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean success;
        private String msgId;
        private String error;
        private int errorCode;
        private int attempts = 1;
        private String channel;

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

        public Builder errorCode(EmailErrorCode errorCode) {
            this.errorCode = errorCode.code();
            return this;
        }

        public Builder attempts(int attempts) {
            this.attempts = attempts;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder channel(EmailChannelType channelType) {
            this.channel = channelType.code();
            return this;
        }

        public Builder from(SendResponse response) {
            this.success = response.success;
            this.msgId = response.msgId;
            this.error = response.error;
            this.errorCode = response.errorCode;
            this.attempts = response.attempts;
            this.channel = response.channel;
            return this;
        }

        public SendResponse build() {
            return new SendResponse(success, msgId, error, errorCode, attempts, channel);
        }
    }
}
