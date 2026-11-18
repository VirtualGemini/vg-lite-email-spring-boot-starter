package com.velox.email.support.type;

public enum LogLevelType {
    ERROR,
    WARN,
    INFO,
    DEBUG;

    public boolean allows(LogLevelType target) {
        return this.ordinal() >= target.ordinal();
    }
}
