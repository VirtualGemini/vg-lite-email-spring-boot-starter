package com.velox.email.api.impl;

import com.velox.email.api.IEmailChannel;

public abstract class AbstractEmailChannel implements IEmailChannel {

    private final String name;

    protected AbstractEmailChannel(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
    }
}
