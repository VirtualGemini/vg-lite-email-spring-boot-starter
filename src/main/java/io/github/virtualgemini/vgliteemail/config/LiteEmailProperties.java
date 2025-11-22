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
package io.github.virtualgemini.vgliteemail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/21 21:48
 */

/**
 * 配置属性 -- Configuration properties
 * 对应 yml lite.email.* 前缀
 */
@ConfigurationProperties(prefix = "vg.lite-email")
public class LiteEmailProperties {
    private String sender;
    private String password;
    private String host;   // 可选，留空则自动推断
    private Integer port;  // 可选
    private boolean ssl = true; // 默认 SSL

    /* ---------- 构造器 ---------- */
    public LiteEmailProperties() { }

    /* ---------- getter / setter ---------- */
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public boolean isSsl() { return ssl; }
    public void setSsl(boolean ssl) { this.ssl = ssl; }
}
