package com.velox.email.exception;

public class VeloxEmailException extends RuntimeException {

    public VeloxEmailException(String message) {
        super(message);
    }

    public VeloxEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
