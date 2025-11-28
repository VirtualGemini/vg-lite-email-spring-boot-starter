package io.github.virtualgemini.vgliteemail.channel.impl;

import io.github.virtualgemini.vgliteemail.api.impl.AbstractEmailChannel;
import io.github.virtualgemini.vgliteemail.channel.meta.SmtpMeta;
import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Concrete SMTP email channel.
 */
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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(request.getTo());
            message.setSubject(request.getSubject());
            message.setText(request.getText());
            sender.send(message);

            return SendResponse.builder().success(true).build();
        } catch (Exception e) {
            return SendResponse.builder().success(false)
                    .error(e.getMessage())
                    .errorCode(-1)
                    .build();
        }
    }

    public static SmtpMeta guessMeta(String senderEmail) {
        String domain = senderEmail.substring(senderEmail.indexOf('@') + 1);
        switch (domain) {
            /* 国内主流 / China Mainland */
            case "qq.com":
                return new SmtpMeta("smtp.qq.com", 465, true);
            case "foxmail.com":
                return new SmtpMeta("smtp.qq.com", 465, true);
            case "163.com":
                return new SmtpMeta("smtp.163.com", 465, true);
            case "126.com":
                return new SmtpMeta("smtp.126.com", 465, true);
            case "yeah.net":
                return new SmtpMeta("smtp.yeah.net", 465, true);
            case "sina.com":
                return new SmtpMeta("smtp.sina.com", 465, true);
            case "sina.cn":
                return new SmtpMeta("smtp.sina.cn", 465, true);
            case "sohu.com":
                return new SmtpMeta("smtp.sohu.com", 465, true);
            case "aliyun.com":
                return new SmtpMeta("smtp.aliyun.com", 465, true);
            case "exmail.qq.com":
                return new SmtpMeta("smtp.exmail.qq.com", 465, true);

            /* 国际主流 / Global Major */
            case "gmail.com":
            case "googlemail.com":
                return new SmtpMeta("smtp.gmail.com", 465, true);
            case "outlook.com":
            case "hotmail.com":
            case "live.com":
                return new SmtpMeta("smtp-mail.outlook.com", 587, false);
            case "yahoo.com":
            case "ymail.com":
                return new SmtpMeta("smtp.mail.yahoo.com", 465, true);
            case "aol.com":
                return new SmtpMeta("smtp.aol.com", 587, false);
            case "icloud.com":
            case "me.com":
            case "mac.com":
                return new SmtpMeta("smtp.mail.me.com", 587, false);

            /* 教育/企业 / Edu & Corp */
            case "csun.edu":
                return new SmtpMeta("smtp.csun.edu", 587, false);

            default:
                return new SmtpMeta("smtp." + domain, 465, true);
        }
    }
}

