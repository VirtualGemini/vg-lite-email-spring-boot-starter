package io.github.virtualgemini.vgliteemail.exception;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 20:53
 */
public class LiteEmailException extends RuntimeException {
    public LiteEmailException(String message) {
        super(message);
    }

    public LiteEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}

