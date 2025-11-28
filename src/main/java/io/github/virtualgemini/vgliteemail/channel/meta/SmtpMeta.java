package io.github.virtualgemini.vgliteemail.channel.meta;

import io.github.virtualgemini.vgliteemail.channel.IProtocolMeta;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 22:09
 */
public class SmtpMeta implements IProtocolMeta {
    private final String host;
    private final int port;
    private final boolean ssl;

    public SmtpMeta(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    public String host() { return host; }
    public int port() { return port; }
    public boolean ssl() { return ssl; }
}

