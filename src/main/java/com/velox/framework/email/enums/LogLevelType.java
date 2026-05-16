package com.velox.email.enums;

public enum LogLevelType {
    ERROR,
    WARN,
    INFO,
    DEBUG;

    public boolean allows(LogLevelType target) {
        return this.ordinal() >= target.ordinal();
    }
}
