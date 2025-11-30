/*
 * Copyright 2025 Your VirtualGemini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.virtualgemini.vgliteemail.utils;

import io.github.virtualgemini.vgliteemail.properties.LiteEmailLoggingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
public final class LiteMailLogUtil {

    private static final Logger LOG = LoggerFactory.getLogger("io.github.virtualgemini.vgliteemail");

    private static volatile boolean enabled = true; // 默认开启


    private static String logLevel = String.valueOf(LogLevel.INFO); // 默认日志级别

    private static final String BANNER_TEXT;

    static {
        String text = ":: VG Lite Mail ::";
        try (InputStream in = LiteMailLogUtil.class.getResourceAsStream("/lite-mail-banner.txt")) {
            if (in != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
                text = new String(out.toByteArray(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}
        BANNER_TEXT = text;
    }
    // 设置日志配置
    public static void setLoggingProperties(LiteEmailLoggingProperties properties) {
        enabled = properties.isEnabled();
        logLevel = properties.getLevel();  // 获取日志级别

    }

    public static void printBanner() {
        if (!enabled) return;
        // ① 先打印无颜色 ASCII
        System.out.println("\n" + BANNER_TEXT);
        // ② 单独给品牌行加青色
        System.out.println("\u001B[36m   :: VG Lite Mail ::  \u001B[0m" + "                     (v" + getVersion() + ")" + "\n");
    }

    private static String getVersion() {
        String v = LiteMailLogUtil.class.getPackage().getImplementationVersion();
        return v == null ? "0.1.64" : v;
    }

    private LiteMailLogUtil() {}

    // 可以由 LiteEmailAutoConfiguration 注入
    public static void setEnabled(boolean value) {
        enabled = value;
    }

    private static boolean isLevelEnabled(LogLevel level) {
        if (logLevel == null) return true; // 默认全部允许
        switch (level) {
            case ERROR:
                return logLevel.equalsIgnoreCase("ERROR");
            case WARN:
                return logLevel.equalsIgnoreCase("WARN") || logLevel.equalsIgnoreCase("ERROR");
            case INFO:
                return logLevel.equalsIgnoreCase("INFO") || logLevel.equalsIgnoreCase("WARN") || logLevel.equalsIgnoreCase("ERROR");
            case DEBUG:
                return logLevel.equalsIgnoreCase("DEBUG") || logLevel.equalsIgnoreCase("INFO")
                        || logLevel.equalsIgnoreCase("WARN") || logLevel.equalsIgnoreCase("ERROR");
            default:
                return true;
        }
    }

    /**
     * 打印纯文本行，不附加 channel/thread 前缀
     */
    public static void infoRaw(String format, Object... args) {
        if (!enabled) return;
        if (LOG.isInfoEnabled()) {
            LOG.info(format, args);
        }
    }

    public static void info(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (!isLevelEnabled(LogLevel.INFO)) return;  // 判断日志级别
        if (LOG.isInfoEnabled()) {
            LOG.info(format(channel, msg), args);   // 保留 channel/thread 格式
        }
    }

    public static void warn(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (!isLevelEnabled(LogLevel.WARN)) return;
        if (LOG.isWarnEnabled()) {
            LOG.warn(format(channel, msg), args);
        }
    }

    public static void error(String channel, String msg, Throwable t) {
        if (!enabled) return;
        if (!isLevelEnabled(LogLevel.ERROR)) return;
        if (LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), t);
        }
    }

    public static void error(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (!isLevelEnabled(LogLevel.ERROR)) return;
        if (LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), args);
        }
    }

    public static void debug(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (!isLevelEnabled(LogLevel.DEBUG)) return;
        if (LOG.isDebugEnabled()) {
            LOG.debug(format(channel, msg), args);
        }
    }

    private static String format(String channel, String msg) {
        return "[" + channel + "]" + msg;
    }
}


