package com.architecturepro.email.autoconfigure;

import com.architecturepro.email.api.IEmailChannel;
import com.architecturepro.email.api.IEmailSender;
import com.architecturepro.email.channel.impl.SmtpEmailChannel;
import com.architecturepro.email.channel.meta.SmtpMeta;
import com.architecturepro.email.config.properties.EmailAsyncProperties;
import com.architecturepro.email.config.properties.LiteEmailLoggingProperties;
import com.architecturepro.email.config.properties.LiteEmailProperties;
import com.architecturepro.email.config.properties.RetryPolicyProperties;
import com.architecturepro.email.core.EmailBuilder;
import com.architecturepro.email.core.EmailChannel;
import com.architecturepro.email.core.EmailSender;
import com.architecturepro.email.enums.ProtocolType;
import com.architecturepro.email.util.LiteMailLogUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
@ConditionalOnProperty(prefix = "vg.lite-email", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        LiteEmailProperties.class,
        EmailAsyncProperties.class,
        RetryPolicyProperties.class,
        LiteEmailLoggingProperties.class
})
public class LiteEmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "liteEmailJavaMailSender")
    public JavaMailSender liteEmailJavaMailSender(LiteEmailProperties properties,
                                                  LiteEmailLoggingProperties loggingProperties) {
        LiteMailLogUtil.setLoggingProperties(loggingProperties);
        properties.validate();

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        ProtocolType protocol = properties.getProtocol();
        if (protocol != ProtocolType.SMTP) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }

        SmtpMeta meta = resolveSmtpMeta(properties);
        properties.setHost(meta.host());
        properties.setPort(meta.port());
        properties.setSsl(meta.ssl());

        sender.setHost(meta.host());
        sender.setPort(meta.port());
        sender.setUsername(properties.getSender());
        sender.setPassword(properties.getPassword());

        Properties mailProperties = sender.getJavaMailProperties();
        mailProperties.setProperty("mail.smtp.auth", "true");
        mailProperties.setProperty("mail.smtp.ssl.enable", Boolean.toString(meta.ssl()));
        mailProperties.setProperty("mail.smtp.starttls.enable", Boolean.toString(!meta.ssl()));
        mailProperties.setProperty("mail.smtp.connectiontimeout", Long.toString(properties.getConnectionTimeout()));
        mailProperties.setProperty("mail.smtp.timeout", Long.toString(properties.getTimeout()));
        mailProperties.setProperty("mail.smtp.writetimeout", Long.toString(properties.getTimeout()));
        return sender;
    }

    @Bean(name = "liteEmailExecutor")
    @ConditionalOnMissingBean(name = "liteEmailExecutor")
    public Executor liteEmailExecutor(EmailAsyncProperties properties) {
        properties.validate();
        if (!properties.isEnabled()) {
            return Runnable::run;
        }
        if (properties.isVirtualThreads()) {
            Semaphore semaphore = new Semaphore(properties.getConcurrencyLimit());
            ExecutorService executorService = Executors.newThreadPerTaskExecutor(
                    Thread.ofVirtual().name(properties.getThreadNamePrefix(), 0).factory()
            );
            return command -> {
                try {
                    semaphore.acquire();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while acquiring email executor permit", exception);
                }
                executorService.execute(() -> {
                    try {
                        command.run();
                    } finally {
                        semaphore.release();
                    }
                });
            };
        }
        return Executors.newFixedThreadPool(
                properties.getConcurrencyLimit(),
                Thread.ofPlatform().name(properties.getThreadNamePrefix(), 0).factory()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public IEmailChannel emailChannel(LiteEmailProperties properties,
                                      @Qualifier("liteEmailJavaMailSender") JavaMailSender mailSender) {
        return new EmailChannel(properties, mailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    public IEmailSender emailSender(IEmailChannel channel,
                                    Executor liteEmailExecutor,
                                    RetryPolicyProperties retryPolicyProperties) {
        return new EmailSender(channel, liteEmailExecutor, retryPolicyProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailBuilder emailBuilder(IEmailSender emailSender) {
        return new EmailBuilder(emailSender);
    }

    private SmtpMeta resolveSmtpMeta(LiteEmailProperties properties) {
        if (properties.getHost() != null
                && !properties.getHost().isBlank()
                && properties.getPort() != null
                && properties.getPort() > 0) {
            return new SmtpMeta(properties.getHost(), properties.getPort(), properties.isSsl());
        }
        return SmtpEmailChannel.guessMeta(properties.getSender());
    }
}
