package com.velox.email.support.util;

import com.velox.email.properties.VeloxEmailLoggingProperties;
import com.velox.email.support.type.LogLevelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VeloxEmailLogUtil {

    private static final Logger LOG = LoggerFactory.getLogger("com.velox.email");
    private static volatile boolean enabled = true;
    private static volatile LogLevelType level = LogLevelType.INFO;

    private VeloxEmailLogUtil() {
    }

    public static void setLoggingProperties(VeloxEmailLoggingProperties properties) {
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
