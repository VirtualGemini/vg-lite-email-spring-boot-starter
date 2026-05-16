package com.architecturepro.email.channel.impl;

import com.architecturepro.email.api.impl.AbstractEmailChannel;
import com.architecturepro.email.channel.meta.SmtpMeta;
import com.architecturepro.email.core.DefaultEmailExceptionTranslator;
import com.architecturepro.email.core.EmailAttachment;
import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;
import com.architecturepro.email.enums.ProtocolType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SmtpEmailChannel extends AbstractEmailChannel {

    private final JavaMailSender sender;
    private final DefaultEmailExceptionTranslator exceptionTranslator = new DefaultEmailExceptionTranslator();

    public SmtpEmailChannel(JavaMailSender sender) {
        super("SMTP");
        this.sender = sender;
    }

    @Override
    public SendResponse send(SendRequest request) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            boolean multipart = request.hasAttachments() || request.hasInlineResources();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, StandardCharsets.UTF_8.name());
            applyAddresses(helper, request);
            applyContent(helper, request);
            applyAttachments(helper, request.attachments());
            applyInlineResources(helper, request.inlineResources());
            sender.send(mimeMessage);
            return SendResponse.builder()
                    .success(true)
                    .msgId(mimeMessage.getMessageID())
                    .build();
        } catch (Exception exception) {
            return exceptionTranslator.translate(name(), request, exception);
        }
    }

    private void applyAddresses(MimeMessageHelper helper, SendRequest request) throws Exception {
        if (request.fromName() != null && !request.fromName().isBlank()) {
            helper.setFrom(new InternetAddress(request.from(), request.fromName(), StandardCharsets.UTF_8.name()));
        } else {
            helper.setFrom(request.from());
        }
        if (request.replyTo() != null && !request.replyTo().isBlank()) {
            helper.setReplyTo(request.replyTo());
        }
        helper.setTo(request.to().toArray(String[]::new));
        if (!request.cc().isEmpty()) {
            helper.setCc(request.cc().toArray(String[]::new));
        }
        if (!request.bcc().isEmpty()) {
            helper.setBcc(request.bcc().toArray(String[]::new));
        }
        helper.setSubject(request.subject() == null ? "" : request.subject());
    }

    private void applyContent(MimeMessageHelper helper, SendRequest request) throws Exception {
        String textBody = request.resolveTextBody();
        String htmlBody = request.resolveHtmlBody();
        if (htmlBody != null && !htmlBody.isBlank()) {
            helper.setText(textBody == null ? "" : textBody, htmlBody);
            return;
        }
        helper.setText(textBody == null ? "" : textBody, request.textBodyAsHtml());
    }

    private void applyAttachments(MimeMessageHelper helper, List<EmailAttachment> attachments) throws Exception {
        for (EmailAttachment attachment : attachments) {
            if (attachment.contentType() != null && !attachment.contentType().isBlank()) {
                helper.addAttachment(attachment.filename(), attachment.source(), attachment.contentType());
            } else {
                helper.addAttachment(attachment.filename(), attachment.source());
            }
        }
    }

    private void applyInlineResources(MimeMessageHelper helper, List<EmailAttachment> inlineResources) throws Exception {
        for (EmailAttachment inlineResource : inlineResources) {
            String contentType = inlineResource.contentType() == null || inlineResource.contentType().isBlank()
                    ? "application/octet-stream"
                    : inlineResource.contentType();
            helper.addInline(inlineResource.contentId(), inlineResource.source(), contentType);
        }
    }

    public static SmtpMeta guessMeta(String senderEmail) {
        if (senderEmail == null || !senderEmail.contains("@")) {
            return new SmtpMeta("localhost", 25, ProtocolType.SMTP, false, false);
        }
        String domain = senderEmail.substring(senderEmail.indexOf('@') + 1);
        return switch (domain) {
            case "qq.com", "foxmail.com" -> new SmtpMeta("smtp.qq.com", 465, ProtocolType.SMTPS, true, false);
            case "163.com" -> new SmtpMeta("smtp.163.com", 465, ProtocolType.SMTPS, true, false);
            case "126.com" -> new SmtpMeta("smtp.126.com", 465, ProtocolType.SMTPS, true, false);
            case "yeah.net" -> new SmtpMeta("smtp.yeah.net", 465, ProtocolType.SMTPS, true, false);
            case "sina.com" -> new SmtpMeta("smtp.sina.com", 465, ProtocolType.SMTPS, true, false);
            case "sina.cn" -> new SmtpMeta("smtp.sina.cn", 465, ProtocolType.SMTPS, true, false);
            case "sohu.com" -> new SmtpMeta("smtp.sohu.com", 465, ProtocolType.SMTPS, true, false);
            case "aliyun.com" -> new SmtpMeta("smtp.aliyun.com", 465, ProtocolType.SMTPS, true, false);
            case "exmail.qq.com" -> new SmtpMeta("smtp.exmail.qq.com", 465, ProtocolType.SMTPS, true, false);
            case "gmail.com", "googlemail.com" -> new SmtpMeta("smtp.gmail.com", 465, ProtocolType.SMTPS, true, false);
            case "outlook.com", "hotmail.com", "live.com" -> new SmtpMeta("smtp-mail.outlook.com", 587, ProtocolType.SMTP, false, true);
            case "yahoo.com", "ymail.com" -> new SmtpMeta("smtp.mail.yahoo.com", 465, ProtocolType.SMTPS, true, false);
            case "aol.com" -> new SmtpMeta("smtp.aol.com", 587, ProtocolType.SMTP, false, true);
            case "icloud.com", "me.com", "mac.com" -> new SmtpMeta("smtp.mail.me.com", 587, ProtocolType.SMTP, false, true);
            default -> new SmtpMeta("smtp." + domain, 465, ProtocolType.SMTPS, true, false);
        };
    }
}
