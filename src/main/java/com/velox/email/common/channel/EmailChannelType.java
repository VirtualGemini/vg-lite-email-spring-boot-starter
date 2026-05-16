package com.velox.email.common.channel;

public enum EmailChannelType {

    SMTP("SMTP");

    private final String code;

    EmailChannelType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
