package com.velox.email.common.message;

public final class EmailCommonMessages {

    public static final String SENDER_MUST_NOT_BE_NULL = "sender must not be null";
    public static final String DEFAULTS_MUST_NOT_BE_NULL = "defaults must not be null";
    public static final String REQUEST_MUST_NOT_BE_NULL = "request must not be null";
    public static final String FILENAME_MUST_NOT_BE_NULL = "filename must not be null";
    public static final String SOURCE_MUST_NOT_BE_NULL = "source must not be null";
    public static final String CHANNEL_MUST_NOT_BE_NULL = "channel must not be null";
    public static final String EXECUTOR_MUST_NOT_BE_NULL = "executor must not be null";
    public static final String RETRY_POLICY_MUST_NOT_BE_NULL = "retryPolicy must not be null";
    public static final String EXCEPTION_TRANSLATOR_MUST_NOT_BE_NULL = "exceptionTranslator must not be null";
    public static final String EMAIL_RECIPIENTS_MUST_NOT_BE_EMPTY = "Email recipients must not be empty";
    public static final String EMAIL_FROM_ADDRESS_MUST_NOT_BE_BLANK = "Email from address must not be blank";
    public static final String ATTACHMENT_RESOURCE_FILENAME_MUST_NOT_BE_BLANK = "Attachment resource filename must not be blank";
    public static final String EMAIL_EXECUTOR_INTERRUPTED = "Interrupted while acquiring email executor permit";
    public static final String EMAIL_HOST_AND_PORT_REQUIRED = "velox.email.host and velox.email.port must be configured when provider-auto-detect=false";
    public static final String EMAIL_USERNAME_MUST_NOT_BE_BLANK = "velox.email.username must not be blank";
    public static final String EMAIL_PASSWORD_MUST_NOT_BE_BLANK = "velox.email.password must not be blank";
    public static final String EMAIL_FROM_MUST_NOT_BE_BLANK = "velox.email.from must not be blank";
    public static final String EMAIL_ASYNC_CONCURRENCY_LIMIT_INVALID = "velox.email.async.concurrency-limit must be >= 1";
    public static final String EMAIL_RETRY_DEFAULT_ATTEMPTS_INVALID = "velox.email.retry.default-attempts must be >= 1";
    public static final String EMAIL_RETRY_MAX_ATTEMPTS_INVALID = "velox.email.retry.max-attempts must be >= 1";
    public static final String EMAIL_RETRY_MULTIPLIER_INVALID = "velox.email.retry.multiplier must be >= 1.0";
    public static final String EMAIL_RETRY_INITIAL_DELAY_INVALID = "velox.email.retry.initial-delay must be >= 0";
    public static final String EMAIL_RETRY_MAX_DELAY_INVALID = "velox.email.retry.max-delay must be >= 0";
    public static final String EMAIL_ATTACHMENT_INPUT_STREAM_READ_FAILED = "Failed to read email attachment input stream";
    public static final String EMAIL_SEND_INTERCEPTOR_BEFORE_RETURNED_NULL = "EmailSendInterceptor.beforeSend must not return null";
    public static final String EMAIL_SEND_INTERCEPTOR_AFTER_RETURNED_NULL = "EmailSendInterceptor.afterSend must not return null";
    public static final String EMAIL_CAPABILITY_DISABLED = "Velox email capability is disabled. Please enable velox.email.enabled=true.";

    private EmailCommonMessages() {
    }
}
