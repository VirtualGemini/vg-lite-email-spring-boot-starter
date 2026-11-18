package com.velox.email.common.error;

import java.util.Arrays;

public enum EmailErrorCode {

    DISABLED(-2, false),
    UNKNOWN(-1, false),
    SMTP_SERVICE_NOT_AVAILABLE(421, true),
    SMTP_MAILBOX_UNAVAILABLE(450, true),
    SMTP_LOCAL_ERROR(451, true),
    SMTP_INSUFFICIENT_STORAGE(452, true);

    private final int code;
    private final boolean retryable;

    EmailErrorCode(int code, boolean retryable) {
        this.code = code;
        this.retryable = retryable;
    }

    public int code() {
        return code;
    }

    public boolean retryable() {
        return retryable;
    }

    public static EmailErrorCode fromCode(int code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.code == code)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
