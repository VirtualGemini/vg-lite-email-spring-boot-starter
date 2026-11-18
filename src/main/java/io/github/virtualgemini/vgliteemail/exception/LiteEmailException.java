package io.github.virtualgemini.vgliteemail.exception;

public class LiteEmailException extends RuntimeException {

    public LiteEmailException(String message) {
        super(message);
    }

    public LiteEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
