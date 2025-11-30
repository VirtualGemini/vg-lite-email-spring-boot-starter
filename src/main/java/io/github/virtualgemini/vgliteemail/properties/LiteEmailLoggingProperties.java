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
package io.github.virtualgemini.vgliteemail.properties;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/29 19:55
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;

@ConfigurationProperties(prefix = "vg.lite-email.logging")
public class LiteEmailLoggingProperties {
    /**
     * 是否开启日志
     */
    private boolean enabled = true;

    /**
     * 日志等级
     */
    private String level = String.valueOf(LogLevel.INFO);

    public LiteEmailLoggingProperties() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

