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
package io.github.virtualgemini.vgliteemail.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/21 22:11
 */


/**
 * 服务器元数据 -- Server metadata
 * 纯 JDK8 记录，无 Lombok
 */
public final class ServerMeta {
    private static final Map<String, ServerMeta> MAP = new HashMap<String, ServerMeta>() {{
        /* 国内主流 / China Mainland */
        put("qq.com",        new ServerMeta("smtp.qq.com", 465, true));
        put("foxmail.com",   new ServerMeta("smtp.qq.com", 465, true));   // 同 QQ
        put("163.com",       new ServerMeta("smtp.163.com", 465, true));
        put("126.com",       new ServerMeta("smtp.126.com", 465, true));
        put("yeah.net",      new ServerMeta("smtp.yeah.net", 465, true));
        put("sina.com",      new ServerMeta("smtp.sina.com", 465, true));
        put("sina.cn",       new ServerMeta("smtp.sina.cn", 465, true));
        put("sohu.com",      new ServerMeta("smtp.sohu.com", 465, true));
        put("aliyun.com",    new ServerMeta("smtp.aliyun.com", 465, true));
        put("exmail.qq.com", new ServerMeta("smtp.exmail.qq.com", 465, true)); // 腾讯企业邮

        /* 国际主流 / Global Major */
        put("gmail.com",     new ServerMeta("smtp.gmail.com", 465, true));
        put("googlemail.com",new ServerMeta("smtp.gmail.com", 465, true));
        put("outlook.com",   new ServerMeta("smtp-mail.outlook.com", 587, false)); // STARTTLS
        put("hotmail.com",   new ServerMeta("smtp-mail.outlook.com", 587, false));
        put("live.com",      new ServerMeta("smtp-mail.outlook.com", 587, false));
        put("yahoo.com",     new ServerMeta("smtp.mail.yahoo.com", 465, true));
        put("ymail.com",     new ServerMeta("smtp.mail.yahoo.com", 465, true));
        put("aol.com",       new ServerMeta("smtp.aol.com", 587, false));
        put("icloud.com",    new ServerMeta("smtp.mail.me.com", 587, false));
        put("me.com",        new ServerMeta("smtp.mail.me.com", 587, false));
        put("mac.com",       new ServerMeta("smtp.mail.me.com", 587, false));

        /* 教育/企业 / Edu & Corp */
        put("csun.edu",      new ServerMeta("smtp.csun.edu", 587, false));   // 加州州立
    }};

    private final String host;
    private final int port;
    private final boolean ssl;

    private ServerMeta(String host, int port, boolean ssl) {
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    /* 静态工厂 -- Static factory */
    public static ServerMeta of(String sender, String host, Integer port, boolean ssl) {
        if (host != null && port != null) {
            return new ServerMeta(host, port, ssl);
        }
        String domain = sender.substring(sender.indexOf('@') + 1);
        ServerMeta meta = MAP.get(domain);
        return meta != null ? meta : new ServerMeta("smtp." + domain, 465, true);
    }

    /* getter */
    public String host() { return host; }
    public int port() { return port; }
    public boolean ssl() { return ssl; }
}