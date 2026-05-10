package com.architecturepro.email.config.properties;

import com.architecturepro.email.enums.ProtocolType;
import com.architecturepro.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vg.lite-email")
public class LiteEmailProperties {

    private boolean enabled = false;
    private String sender;
    private String password;
    private String host;
    private Integer port;
    private boolean ssl = true;
    private ProtocolType protocol = ProtocolType.SMTP;
    private long connectionTimeout = 5000;
    private long timeout = 5000;

    public void validate() {
        if (!enabled) {
            return;
        }
        if (sender == null || sender.isBlank()) {
            throw new EmailConfigException("vg.lite-email.sender must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new EmailConfigException("vg.lite-email.password must not be blank");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolType protocol) {
        this.protocol = protocol;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
