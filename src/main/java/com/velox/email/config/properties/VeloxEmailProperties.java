package com.velox.email.config.properties;

import com.velox.email.enums.ProtocolType;
import com.velox.email.exception.EmailConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "velox.email")
public class VeloxEmailProperties {

    private boolean enabled = false;
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String from;
    private String fromName;
    private String replyTo;
    private Boolean ssl;
    private Boolean starttls;
    private boolean auth = true;
    private boolean providerAutoDetect = true;
    private ProtocolType protocol;
    private long connectionTimeout = 5000;
    private long timeout = 5000;
    private long writeTimeout = 5000;

    public void validate() {
        validateForSmtp();
    }

    public void validateForSmtp() {
        if (!enabled) {
            return;
        }
        if (username == null || username.isBlank()) {
            throw new EmailConfigException("velox.email.username must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new EmailConfigException("velox.email.password must not be blank");
        }
        if ((from == null || from.isBlank()) && (username == null || username.isBlank())) {
            throw new EmailConfigException("velox.email.from must not be blank");
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from == null || from.isBlank() ? username : from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public Boolean getStarttls() {
        return starttls;
    }

    public void setStarttls(Boolean starttls) {
        this.starttls = starttls;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isProviderAutoDetect() {
        return providerAutoDetect;
    }

    public void setProviderAutoDetect(boolean providerAutoDetect) {
        this.providerAutoDetect = providerAutoDetect;
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

    public long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getSender() {
        return getFrom();
    }

    public void setSender(String sender) {
        if (username == null || username.isBlank()) {
            this.username = sender;
        }
        if (from == null || from.isBlank()) {
            this.from = sender;
        }
    }
}
