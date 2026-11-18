package io.github.virtualgemini.vgliteemail.spi.channel;

import io.github.virtualgemini.vgliteemail.api.channel.IEmailChannel;
import io.github.virtualgemini.vgliteemail.common.channel.EmailChannelType;

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
