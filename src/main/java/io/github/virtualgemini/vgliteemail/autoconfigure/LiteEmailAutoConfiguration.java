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

import io.github.virtualgemini.vgliteemail.config.EmailAsyncProperties;
import io.github.virtualgemini.vgliteemail.config.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.config.RetryPolicy;
import io.github.virtualgemini.vgliteemail.core.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自动配置 / Auto-configuration
 */
@AutoConfiguration
@EnableConfigurationProperties({LiteEmailProperties.class, EmailAsyncProperties.class, RetryPolicy.class})
public class LiteEmailAutoConfiguration {

    /* ========== 私有工具方法 ========== */
    private JavaMailSender buildMailSender(LiteEmailProperties prop) {
        JavaMailSenderImpl impl = new JavaMailSenderImpl();
        ServerMeta meta = ServerMeta.of(prop.getSender(), prop.getHost(), prop.getPort(), prop.isSsl());
        impl.setHost(meta.host());
        impl.setPort(meta.port());
        impl.setUsername(prop.getSender());   // 记住用户名 / remember username
        impl.setPassword(prop.getPassword());

        Properties p = impl.getJavaMailProperties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.ssl.enable", String.valueOf(meta.ssl()));
        p.setProperty("mail.smtp.connectiontimeout", "5000");
        p.setProperty("mail.smtp.timeout", "5000");
        return impl;
    }

    /* ========== Spring Bean ========== */
    @Bean
    @ConditionalOnMissingBean
    @Primary
    public JavaMailSender javaMailSender(LiteEmailProperties prop) {
        return buildMailSender(prop);
    }

    @Bean
    @ConditionalOnMissingBean
    @Primary
    public Channel smtpChannel(JavaMailSender sender, LiteEmailProperties prop) {
        return new SmtpChannel(sender, prop.getSender());   // 传入用户名 / pass username
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy() {
        return new RetryPolicy();
    }


    @Bean
    @ConditionalOnMissingBean
    @Primary
    public Executor emailExecutor(EmailAsyncProperties props) {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(props.getCorePoolSize());
        exec.setMaxPoolSize(props.getMaxPoolSize());
        exec.setQueueCapacity(props.getQueueCapacity());
        exec.setThreadNamePrefix(props.getThreadNamePrefix());
        exec.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        exec.initialize();
        return exec;
    }

    @Bean
    @ConditionalOnMissingBean// 自动配置方法参数去掉限定
    @Primary
    public LiteEmailSender liteEmailSender(Channel channel, Executor executor, RetryPolicy retryPolicy) {
        return new LiteEmailSender(channel, executor, retryPolicy);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailBuilder emailBuilder(LiteEmailSender liteEmailSender) {
        return new EmailBuilder(liteEmailSender);
    }

}