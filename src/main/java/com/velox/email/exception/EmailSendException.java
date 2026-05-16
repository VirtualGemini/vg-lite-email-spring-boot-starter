package com.velox.email.exception;

public class EmailSendException extends VeloxEmailException {

    public EmailSendException(String message) {
        super(message);
    }

    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
