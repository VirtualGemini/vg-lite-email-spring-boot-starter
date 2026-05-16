package com.velox.email.spi.channel;

import com.velox.email.api.channel.IEmailChannel;
import com.velox.email.common.channel.EmailChannelType;

public abstract class AbstractEmailChannel implements IEmailChannel {

    private final String name;

    protected AbstractEmailChannel(String name) {
        this.name = name;
    }

    protected AbstractEmailChannel(EmailChannelType channelType) {
        this(channelType.code());
    }

    @Override
    public String name() {
        return name;
    }
}
