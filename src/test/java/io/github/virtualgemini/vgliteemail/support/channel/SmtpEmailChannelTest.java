package io.github.virtualgemini.vgliteemail.support.channel;

import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.core.policy.DefaultRetryPolicy;
import io.github.virtualgemini.vgliteemail.core.sender.DefaultEmailExceptionTranslator;
import io.github.virtualgemini.vgliteemail.core.sender.DefaultEmailSender;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicyProperties;
import io.github.virtualgemini.vgliteemail.support.type.LogLevelType;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SmtpEmailChannelTest {

    @Test
    void sendsPlainTextWithAddressesAndUtf8() throws Exception {
        CapturingMailSender mail = new CapturingMailSender();
        SendResponse response = builder(mail).to("user@example.com").cc("copy@example.com")
                .bcc("hidden@example.com").replyTo("reply@example.com").fromName("邮件服务")
                .subject("你好").text("正文").send();
        assertThat(response.success()).isTrue();
        assertThat(mail.messages).hasSize(1);
        MimeMessage message = mail.messages.getFirst();
        assertThat(message.getSubject()).isEqualTo("你好");
        assertThat(message.getAllRecipients()).hasSize(3);
        assertThat(message.getReplyTo()[0].toString()).isEqualTo("reply@example.com");
        assertThat(message.getContent()).isEqualTo("正文");
    }

    @Test
    void sendsHtmlWithoutRequiringAnAttachment() throws Exception {
        CapturingMailSender mail = new CapturingMailSender();
        SendResponse response = builder(mail).to("user@example.com").html("<h1>Welcome</h1>").send();
        assertThat(response.success()).isTrue();
        assertThat(parts(mail.messages.getFirst())).anySatisfy(part -> {
            try {
                assertThat(part.isMimeType("text/html")).isTrue();
                assertThat(part.getContent()).isEqualTo("<h1>Welcome</h1>");
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        });
    }

    @Test
    void sendsTextAlternativeAttachmentsAndInlineResources() throws Exception {
        CapturingMailSender mail = new CapturingMailSender();
        SendResponse response = builder(mail).to("user@example.com").text("Plain alternative")
                .html("<img src='cid:logo'>")
                .attachment("report.txt", "report".getBytes(StandardCharsets.UTF_8), "text/plain")
                .inline("logo", "logo.png", new byte[]{1, 2, 3}, "image/png").send();
        assertThat(response.success()).isTrue();
        List<Part> parts = parts(mail.messages.getFirst());
        assertThat(parts).anySatisfy(part -> {
            try {
                assertThat(part.getFileName()).isEqualTo("report.txt");
                assertThat(part.getInputStream().readAllBytes()).isEqualTo("report".getBytes(StandardCharsets.UTF_8));
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        });
        assertThat(parts).anySatisfy(part -> {
            try {
                assertThat(part.getHeader("Content-ID")).containsExactly("<logo>");
            } catch (Exception exception) {
                throw new AssertionError(exception);
            }
        });
    }

    @Test
    void authenticationFailureReturnsFailedResponse() {
        CapturingMailSender mail = new CapturingMailSender() {
            @Override
            public void send(MimeMessage message) {
                throw new MailAuthenticationException("535 Authentication failed");
            }
        };
        SendResponse response = builder(mail).to("user@example.com").retry(3).text("test").send();
        assertThat(response.success()).isFalse();
        assertThat(response.errorCode()).isEqualTo(535);
        assertThat(response.attempts()).isEqualTo(1);
    }

    private EmailBuilder builder(JavaMailSenderImpl mail) {
        return new EmailBuilder(new DefaultEmailSender(new SmtpEmailChannel(mail),
                new LiteEmailLogger(false, LogLevelType.INFO), Runnable::run,
                new DefaultRetryPolicy(new RetryPolicyProperties()), new DefaultEmailExceptionTranslator(),
                List.of(), List.of(), SendRequest.builder().from("sender@example.com").build()));
    }

    private List<Part> parts(Part part) throws Exception {
        List<Part> parts = new ArrayList<>();
        parts.add(part);
        if (part.getContent() instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                parts.addAll(parts(multipart.getBodyPart(i)));
            }
        }
        return parts;
    }

    private static class CapturingMailSender extends JavaMailSenderImpl {
        private final List<MimeMessage> messages = new ArrayList<>();

        @Override
        public void send(MimeMessage message) {
            try {
                message.saveChanges();
                messages.add(message);
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        }
    }
}
