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

package io.github.virtualgemini.vgliteemail.autoconfigure;

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.channel.impl.SmtpEmailChannel;
import io.github.virtualgemini.vgliteemail.channel.meta.SmtpMeta;
import io.github.virtualgemini.vgliteemail.core.EmailBuilder;
import io.github.virtualgemini.vgliteemail.core.EmailChannel;
import io.github.virtualgemini.vgliteemail.core.EmailSender;
import io.github.virtualgemini.vgliteemail.enums.ProtocolEnum;
import io.github.virtualgemini.vgliteemail.properties.EmailAsyncProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailLoggingProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicyProperties;
import io.github.virtualgemini.vgliteemail.utils.LiteMailLogUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 自动配置 / Auto-configuration
 */
@AutoConfiguration
@EnableConfigurationProperties(
        {
                LiteEmailProperties.class,
                EmailAsyncProperties.class,
                RetryPolicyProperties.class,
                LiteEmailLoggingProperties.class
        })
public class LiteEmailAutoConfiguration {

    private final LiteEmailLoggingProperties liteEmailLoggingProperties;

    // 构造方法注入（Spring 会自动传入配置类实例）
    public LiteEmailAutoConfiguration(LiteEmailLoggingProperties liteEmailLoggingProperties) {
        this.liteEmailLoggingProperties = liteEmailLoggingProperties;
    }

    /* ========== 私有工具方法 ========== */
    private JavaMailSender buildEmailClient(LiteEmailProperties prop) {
        LiteMailLogUtil.setLoggingProperties(this.liteEmailLoggingProperties);
        ProtocolEnum protocolEnum = prop.getProtocol() != null ? ProtocolEnum.valueOf(prop.getProtocol()) : ProtocolEnum.SMTP;
        switch (protocolEnum) {
            case SMTP:
                JavaMailSenderImpl impl = new JavaMailSenderImpl();

                SmtpMeta meta;
                if (prop.getHost() != null && prop.getPort() != null) {
                    meta = new SmtpMeta(prop.getHost(), prop.getPort(), prop.isSsl());
                } else {
                    meta = SmtpEmailChannel.guessMeta(prop.getSender());
                    prop.setHost(meta.host());
                    prop.setPort(meta.port());
                    prop.setSsl(meta.ssl());
                }

                impl.setHost(meta.host());
                impl.setPort(meta.port());
                impl.setUsername(prop.getSender());
                impl.setPassword(prop.getPassword());

                Properties p = impl.getJavaMailProperties();
                p.setProperty("mail.smtp.auth", "true");
                p.setProperty("mail.smtp.ssl.enable", String.valueOf(meta.ssl()));
                p.setProperty("mail.smtp.connectiontimeout", String.valueOf(prop.getConnectionTimeout()));
                p.setProperty("mail.smtp.timeout", String.valueOf(prop.getTimeout()));

                return impl;
//            case API:

            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocolEnum);
        }
    }


    /* ========== Spring Bean ========== */
    @Bean
    public boolean initLogging(LiteEmailLoggingProperties loggingProperties) {
        LiteMailLogUtil.setEnabled(loggingProperties.isEnabled());
        return true;
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public JavaMailSender javaMailSender(LiteEmailProperties prop) {
        prop.validate(); // 校验必填字段
        return buildEmailClient(prop);
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public EmailChannel emailChannel(LiteEmailProperties properties, JavaMailSender mailSender) {
        return new EmailChannel(properties, mailSender);
    }


    @Bean
    @ConditionalOnMissingBean// 自动配置方法参数去掉限定
    @Primary
    public EmailSender liteEmailSender(IEmailChannel channel, Executor executor, RetryPolicyProperties retryPolicyProperties) {
        return new EmailSender(channel, executor, retryPolicyProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public EmailBuilder emailBuilder(EmailSender emailSender) {
        return new EmailBuilder(emailSender);
    }

}