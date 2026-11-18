package com.velox.email.support.channel;

import com.velox.email.api.message.EmailAttachment;
import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import com.velox.email.common.channel.EmailChannelType;
import com.velox.email.common.provider.SmtpProviderDomains;
import com.velox.email.common.provider.SmtpProviderHosts;
import com.velox.email.common.provider.SmtpProviderPorts;
import com.velox.email.core.sender.DefaultEmailExceptionTranslator;
import com.velox.email.spi.channel.AbstractEmailChannel;
import com.velox.email.support.meta.SmtpMeta;
import com.velox.email.support.type.ProtocolType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SmtpEmailChannel extends AbstractEmailChannel {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();
    private static final String DEFAULT_INLINE_CONTENT_TYPE = "application/octet-stream";
    private static final String EMAIL_DOMAIN_SEPARATOR = "@";

    private final JavaMailSender sender;
    private final DefaultEmailExceptionTranslator exceptionTranslator = new DefaultEmailExceptionTranslator();

    public SmtpEmailChannel(JavaMailSender sender) {
        super(EmailChannelType.SMTP);
        this.sender = sender;
    }

    @Override
    public SendResponse send(SendRequest request) {
        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            boolean multipart = request.hasAttachments() || request.hasInlineResources();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, UTF_8);
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
            helper.setFrom(new InternetAddress(request.from(), request.fromName(), UTF_8));
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
                    ? DEFAULT_INLINE_CONTENT_TYPE
                    : inlineResource.contentType();
            helper.addInline(inlineResource.contentId(), inlineResource.source(), contentType);
        }
    }

    public static SmtpMeta guessMeta(String senderEmail) {
        if (senderEmail == null || !senderEmail.contains(EMAIL_DOMAIN_SEPARATOR)) {
            return new SmtpMeta(SmtpProviderHosts.LOCALHOST, SmtpProviderPorts.LOCALHOST, ProtocolType.SMTP, false, false);
        }
        String domain = senderEmail.substring(senderEmail.indexOf(EMAIL_DOMAIN_SEPARATOR) + 1);
        return switch (domain) {
            case SmtpProviderDomains.QQ, SmtpProviderDomains.FOXMAIL ->
                    new SmtpMeta(SmtpProviderHosts.QQ, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.NETEASE_163 ->
                    new SmtpMeta(SmtpProviderHosts.NETEASE_163, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.NETEASE_126 ->
                    new SmtpMeta(SmtpProviderHosts.NETEASE_126, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.YEAH ->
                    new SmtpMeta(SmtpProviderHosts.YEAH, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.SINA ->
                    new SmtpMeta(SmtpProviderHosts.SINA, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.SINA_CN ->
                    new SmtpMeta(SmtpProviderHosts.SINA_CN, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.SOHU ->
                    new SmtpMeta(SmtpProviderHosts.SOHU, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.ALIYUN ->
                    new SmtpMeta(SmtpProviderHosts.ALIYUN, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.EXMAIL_QQ ->
                    new SmtpMeta(SmtpProviderHosts.EXMAIL_QQ, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.GMAIL, SmtpProviderDomains.GOOGLEMAIL ->
                    new SmtpMeta(SmtpProviderHosts.GMAIL, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.OUTLOOK, SmtpProviderDomains.HOTMAIL, SmtpProviderDomains.LIVE ->
                    new SmtpMeta(SmtpProviderHosts.OUTLOOK, SmtpProviderPorts.SMTP_STARTTLS, ProtocolType.SMTP, false, true);
            case SmtpProviderDomains.YAHOO, SmtpProviderDomains.YMAIL ->
                    new SmtpMeta(SmtpProviderHosts.YAHOO, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
            case SmtpProviderDomains.AOL ->
                    new SmtpMeta(SmtpProviderHosts.AOL, SmtpProviderPorts.SMTP_STARTTLS, ProtocolType.SMTP, false, true);
            case SmtpProviderDomains.ICLOUD, SmtpProviderDomains.ME, SmtpProviderDomains.MAC ->
                    new SmtpMeta(SmtpProviderHosts.APPLE, SmtpProviderPorts.SMTP_STARTTLS, ProtocolType.SMTP, false, true);
            default ->
                    new SmtpMeta(SmtpProviderHosts.PREFIX + domain, SmtpProviderPorts.SMTPS, ProtocolType.SMTPS, true, false);
        };
    }
}
