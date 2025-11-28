package io.github.virtualgemini.vgliteemail.exception;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 20:54
 */
public final class EmailExceptions {

    private EmailExceptions() {}

    public static EmailConfigException missing(String field) {
        return new EmailConfigException("Required email config missing: " + field);
    }

    public static EmailSendException sendError(Exception e) {
        return new EmailSendException("Failed to send email", e);
    }

    public static EmailRetryException retryFail(int times) {
        return new EmailRetryException("Retry failed after " + times + " attempts");
    }
}

