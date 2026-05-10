package com.architecturepro.email.channel.impl;

import com.architecturepro.email.api.impl.AbstractEmailChannel;
import com.architecturepro.email.channel.meta.SmtpMeta;
import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailChannel extends AbstractEmailChannel {

    private final JavaMailSender sender;
    private final String from;

    public SmtpEmailChannel(JavaMailSender sender, String from) {
        super("SMTP");
        this.sender = sender;
        this.from = from;
    }

    @Override
    public SendResponse send(SendRequest request) {
        try {
            if (request.html()) {
                MimeMessage mimeMessage = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(from);
                helper.setTo(request.to());
                helper.setSubject(request.subject());
                helper.setText(request.text(), true);
                sender.send(mimeMessage);
            } else {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from);
                message.setTo(request.to());
                message.setSubject(request.subject());
                message.setText(request.text());
                sender.send(message);
            }
            return SendResponse.builder().success(true).build();
        } catch (Exception exception) {
            return SendResponse.builder()
                    .success(false)
                    .error(exception.getMessage())
                    .errorCode(-1)
                    .build();
        }
    }

    public static SmtpMeta guessMeta(String senderEmail) {
        String domain = senderEmail.substring(senderEmail.indexOf('@') + 1);
        return switch (domain) {
            case "qq.com", "foxmail.com" -> new SmtpMeta("smtp.qq.com", 465, true);
            case "163.com" -> new SmtpMeta("smtp.163.com", 465, true);
            case "126.com" -> new SmtpMeta("smtp.126.com", 465, true);
            case "yeah.net" -> new SmtpMeta("smtp.yeah.net", 465, true);
            case "sina.com" -> new SmtpMeta("smtp.sina.com", 465, true);
            case "sina.cn" -> new SmtpMeta("smtp.sina.cn", 465, true);
            case "sohu.com" -> new SmtpMeta("smtp.sohu.com", 465, true);
            case "aliyun.com" -> new SmtpMeta("smtp.aliyun.com", 465, true);
            case "exmail.qq.com" -> new SmtpMeta("smtp.exmail.qq.com", 465, true);
            case "gmail.com", "googlemail.com" -> new SmtpMeta("smtp.gmail.com", 465, true);
            case "outlook.com", "hotmail.com", "live.com" -> new SmtpMeta("smtp-mail.outlook.com", 587, false);
            case "yahoo.com", "ymail.com" -> new SmtpMeta("smtp.mail.yahoo.com", 465, true);
            case "aol.com" -> new SmtpMeta("smtp.aol.com", 587, false);
            case "icloud.com", "me.com", "mac.com" -> new SmtpMeta("smtp.mail.me.com", 587, false);
            default -> new SmtpMeta("smtp." + domain, 465, true);
        };
    }
}
