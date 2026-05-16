package com.velox.email.properties;

import com.velox.email.support.type.LogLevelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "velox.email.logging")
public class VeloxEmailLoggingProperties {

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
