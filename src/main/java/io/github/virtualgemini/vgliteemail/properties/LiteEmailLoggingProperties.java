package io.github.virtualgemini.vgliteemail.properties;

import io.github.virtualgemini.vgliteemail.common.prefix.EmailPropertyPrefixes;
import io.github.virtualgemini.vgliteemail.support.type.LogLevelType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = EmailPropertyPrefixes.EMAIL_LOGGING)
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
