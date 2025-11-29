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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class LiteMailLog {

    private static final Logger LOG = LoggerFactory.getLogger("io.github.virtualgemini.vgliteemail");

    private static volatile boolean enabled = true; // 默认开启

    private static final String BANNER_TEXT;

    static {
        String text = ":: VG Lite Mail ::";
        try (InputStream in = LiteMailLog.class.getResourceAsStream("/lite-mail-banner.txt")) {
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

    public static void printBanner() {
        if (!enabled) return;
        // ① 先打印无颜色 ASCII
        System.out.println("\n" + BANNER_TEXT);
        // ② 单独给品牌行加青色
        System.out.println("\u001B[36m   :: VG Lite Mail ::  \u001B[0m" + "                     (v" + getVersion() + ")" + "\n");
    }

    private static String getVersion() {
        String v = LiteMailLog.class.getPackage().getImplementationVersion();
        return v == null ? "0.1.44" : v;
    }

    private LiteMailLog() {}

    // 可以由 LiteEmailAutoConfiguration 注入
    public static void setEnabled(boolean value) {
        enabled = value;
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
        if (LOG.isInfoEnabled()) {
            LOG.info(format(channel, msg), args);
        }
    }

    public static void warn(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (LOG.isWarnEnabled()) {
            LOG.warn(format(channel, msg), args);
        }
    }

    public static void error(String channel, String msg, Throwable t) {
        if (!enabled) return;
        if (LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), t);
        }
    }

    public static void error(String channel, String msg, Object... args) {
        if (!enabled) return;
        if (LOG.isErrorEnabled()) {
            LOG.error(format(channel, msg), args);
        }
    }

    private static String format(String channel, String msg) {
        String thread = Thread.currentThread().getName();
        return "[channel=" + channel + "][thread=" + thread + "] " + msg;
    }
}


