package com.architecturepro.email.util;

import com.architecturepro.email.config.properties.LiteEmailLoggingProperties;
import com.architecturepro.email.enums.LogLevelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LiteMailLogUtil {

    private static final Logger LOG = LoggerFactory.getLogger("com.architecturepro.email");
    private static volatile boolean enabled = true;
    private static volatile LogLevelType level = LogLevelType.INFO;

    private LiteMailLogUtil() {
    }

    public static void setLoggingProperties(LiteEmailLoggingProperties properties) {
        enabled = properties.isEnabled();
        level = properties.getLevel();
    }

    public static void info(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.INFO) && LOG.isInfoEnabled()) {
            LOG.info(format(channel, msg), args);
        }
    }

    public static void warn(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.WARN) && LOG.isWarnEnabled()) {
            LOG.warn(format(channel, msg), args);
        }
    }

    public static void error(String channel, String msg, Object... args) {
        if (enabled && level.allows(LogLevelType.ERROR) && LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), args);
        }
    }

    private static String format(String channel, String msg) {
        return "[" + channel + "] " + msg;
    }
}
