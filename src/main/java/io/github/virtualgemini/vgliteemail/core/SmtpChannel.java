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

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: SMTP 渠道 / SMTP channel
 * @createDate 2025/11/21 21:46
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * SMTP 渠道实现 -- SMTP channel implementation
 */
public class SmtpChannel implements Channel {

    private static final Logger log = LoggerFactory.getLogger(SmtpChannel.class);
    private final JavaMailSender mailSender;
    private final String username;   // <-- 新增

    public SmtpChannel(JavaMailSender mailSender, String username) {
        this.mailSender = mailSender;
        this.username = username;
    }

    @Override
    public String name() { return "smtp"; }

    @Override
    public SendResponse send(SendRequest request) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setFrom(this.username);   // 使用本地字段，不再调用 getUsername()
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getText(), request.isHtml());
            mailSender.send(msg);
            return SendResponse.builder().success(true).msgId(msg.getMessageID()).build();
        } catch (MessagingException e) {
            log.error("SMTP send failed", e);
            return SendResponse.builder().success(false).error(e.getMessage()).errorCode(-1).build();
        }
    }

    @Override
    public boolean retryable(int errorCode) {
        return errorCode == 421 || errorCode == 451 || errorCode == 452;
    }
}
