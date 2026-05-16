package com.velox.email.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultEmailExceptionTranslator implements EmailExceptionTranslator {

    private static final Pattern SMTP_CODE_PATTERN = Pattern.compile("\\b([245]\\d{2})\\b");

    @Override
    public SendResponse translate(String channelName, SendRequest request, Throwable throwable) {
        String message = throwable.getMessage();
        return SendResponse.builder()
                .success(false)
                .error(message == null || message.isBlank() ? throwable.getClass().getSimpleName() : message)
                .errorCode(extractErrorCode(throwable))
                .channel(channelName)
                .build();
    }

    private int extractErrorCode(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                Matcher matcher = SMTP_CODE_PATTERN.matcher(message);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group(1));
                }
            }
            current = current.getCause();
        }
        return -1;
    }
}
