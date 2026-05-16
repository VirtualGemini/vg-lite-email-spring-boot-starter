package com.architecturepro.email.config.properties;

import com.architecturepro.email.enums.LogLevelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "architecture.email.logging")
public class LiteEmailLoggingProperties {

    private boolean enabled = true;
    private LogLevelType level = LogLevelType.INFO;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LogLevelType getLevel() {
        return level;
    }

    public void setLevel(LogLevelType level) {
        this.level = level;
    }
}
