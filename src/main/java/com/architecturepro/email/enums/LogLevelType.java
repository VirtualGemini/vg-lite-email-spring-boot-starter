package com.architecturepro.email.enums;

public enum LogLevelType {
    ERROR,
    WARN,
    INFO,
    DEBUG;

    public boolean allows(LogLevelType target) {
        return this.ordinal() >= target.ordinal();
    }
}
