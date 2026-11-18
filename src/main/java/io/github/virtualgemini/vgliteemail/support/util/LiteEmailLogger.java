package io.github.virtualgemini.vgliteemail.support.util;

import io.github.virtualgemini.vgliteemail.support.type.LogLevelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LiteEmailLogger {

    private static final String LOGGER_NAME = "io.github.virtualgemini.vgliteemail";
    private static final String CHANNEL_PREFIX_FORMAT = "[%s] %s";
    private static final Logger LOG = LoggerFactory.getLogger(LOGGER_NAME);

    private final boolean enabled;
    private final LogLevelType level;

    public LiteEmailLogger(boolean enabled, LogLevelType level) {
        this.enabled = enabled;
        this.level = level != null ? level : LogLevelType.INFO;
    }

    public void info(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.INFO) && LOG.isInfoEnabled()) {
            LOG.info(format(channel, msg), args);
        }
    }

    public void warn(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.WARN) && LOG.isWarnEnabled()) {
            LOG.warn(format(channel, msg), args);
        }
    }

    public void error(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.ERROR) && LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), args);
        }
    }

    private String format(String channel, String msg) {
        return CHANNEL_PREFIX_FORMAT.formatted(channel, msg);
    }
}
