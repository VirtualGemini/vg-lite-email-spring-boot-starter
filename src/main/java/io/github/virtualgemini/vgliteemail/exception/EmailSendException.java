package io.github.virtualgemini.vgliteemail.exception;

public class EmailSendException extends LiteEmailException {

    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
