package com.velox.email.autoconfigure;

import com.velox.email.api.IEmailChannel;
import com.velox.email.api.IEmailSender;
import com.velox.email.channel.impl.SmtpEmailChannel;
import com.velox.email.channel.meta.SmtpMeta;
import com.velox.email.config.properties.EmailAsyncProperties;
import com.velox.email.config.properties.VeloxEmailLoggingProperties;
import com.velox.email.config.properties.VeloxEmailProperties;
import com.velox.email.config.properties.RetryPolicyProperties;
import com.velox.email.core.DefaultEmailBuilderFactory;
import com.velox.email.core.DefaultEmailExceptionTranslator;
import com.velox.email.core.DefaultEmailSender;
import com.velox.email.core.DefaultRetryPolicy;
import com.velox.email.core.EmailBuilder;
import com.velox.email.core.EmailBuilderFactory;
import com.velox.email.core.EmailExceptionTranslator;
import com.velox.email.core.EmailSendInterceptor;
import com.velox.email.core.EmailSendListener;
import com.velox.email.core.EmailChannel;
import com.velox.email.core.EmailSender;
import com.velox.email.core.RetryPolicy;
import com.velox.email.core.SendRequest;
import com.velox.email.enums.ProtocolType;
import com.velox.email.util.VeloxEmailLogUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@AutoConfiguration
@ConditionalOnClass(JavaMailSender.class)
@ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        VeloxEmailProperties.class,
        EmailAsyncProperties.class,
        RetryPolicyProperties.class,
        VeloxEmailLoggingProperties.class
})
public class VeloxEmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "veloxEmailJavaMailSender", value = {EmailChannel.class, EmailSender.class})
    public JavaMailSender veloxEmailJavaMailSender(VeloxEmailProperties properties,
                                                          VeloxEmailLoggingProperties loggingProperties) {
        VeloxEmailLogUtil.setLoggingProperties(loggingProperties);
        properties.validateForSmtp();

        SmtpMeta meta = resolveSmtpMeta(properties);
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(meta.host());
        sender.setPort(meta.port());
        sender.setProtocol("smtp");
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setDefaultEncoding("UTF-8");

        Properties mailProperties = sender.getJavaMailProperties();
        mailProperties.setProperty("mail.transport.protocol", sender.getProtocol());
        mailProperties.setProperty("mail.smtp.auth", Boolean.toString(properties.isAuth()));
        mailProperties.setProperty("mail.smtp.ssl.enable", Boolean.toString(meta.ssl()));
        mailProperties.setProperty("mail.smtp.starttls.enable", Boolean.toString(meta.starttls()));
        mailProperties.setProperty("mail.smtp.connectiontimeout", Long.toString(properties.getConnectionTimeout()));
        mailProperties.setProperty("mail.smtp.timeout", Long.toString(properties.getTimeout()));
        mailProperties.setProperty("mail.smtp.writetimeout", Long.toString(properties.getWriteTimeout()));
        return sender;
    }

    @Bean(name = "veloxEmailExecutor")
    @ConditionalOnMissingBean(name = "veloxEmailExecutor")
    public Executor veloxEmailExecutor(EmailAsyncProperties properties) {
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
    public RetryPolicy retryPolicy(RetryPolicyProperties properties) {
        properties.validate();
        return new DefaultRetryPolicy(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public EmailExceptionTranslator emailExceptionTranslator() {
        return new DefaultEmailExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(name = "veloxEmailJavaMailSender")
    public IEmailChannel emailChannel(@Qualifier("veloxEmailJavaMailSender") JavaMailSender mailSender) {
        return new SmtpEmailChannel(mailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    public IEmailSender emailSender(EmailChannel channel,
                                    @Qualifier("veloxEmailExecutor") Executor executor,
                                    RetryPolicy retryPolicy,
                                    EmailExceptionTranslator exceptionTranslator,
                                    VeloxEmailProperties properties,
                                    ObjectProvider<EmailSendInterceptor> interceptorsProvider,
                                    ObjectProvider<EmailSendListener> listenersProvider) {
        List<EmailSendInterceptor> interceptors = interceptorsProvider.orderedStream().collect(Collectors.toList());
        List<EmailSendListener> listeners = listenersProvider.orderedStream().collect(Collectors.toList());
        return new DefaultEmailSender(channel, executor, retryPolicy, exceptionTranslator, interceptors, listeners, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilderFactory.class)
    public EmailBuilderFactory<EmailBuilder> emailBuilderFactory(EmailSender emailSender, VeloxEmailProperties properties) {
        return new DefaultEmailBuilderFactory(emailSender, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilder.class)
    @ConditionalOnBean(EmailBuilderFactory.class)
    public EmailBuilder emailBuilder(EmailBuilderFactory<EmailBuilder> emailBuilderFactory) {
        return emailBuilderFactory.newMessage();
    }

    private SmtpMeta resolveSmtpMeta(VeloxEmailProperties properties) {
        boolean hasHost = properties.getHost() != null && !properties.getHost().isBlank();
        boolean hasPort = properties.getPort() != null && properties.getPort() > 0;
        if (hasHost && hasPort) {
            ProtocolType protocol = properties.getProtocol() != null
                    ? properties.getProtocol()
                    : ((properties.getSsl() != null && properties.getSsl()) ? ProtocolType.SMTPS : ProtocolType.SMTP);
            boolean ssl = properties.getSsl() != null ? properties.getSsl() : protocol == ProtocolType.SMTPS;
            boolean starttls = properties.getStarttls() != null ? properties.getStarttls() : (!ssl && protocol == ProtocolType.SMTP);
            return new SmtpMeta(properties.getHost(), properties.getPort(), protocol, ssl, starttls);
        }

        if (!properties.isProviderAutoDetect()) {
            throw new IllegalArgumentException("velox.email.host and velox.email.port must be configured when provider-auto-detect=false");
        }

        SmtpMeta detected = SmtpEmailChannel.guessMeta(properties.getUsername());
        boolean ssl = properties.getSsl() != null ? properties.getSsl() : detected.ssl();
        boolean starttls = properties.getStarttls() != null ? properties.getStarttls() : detected.starttls();
        ProtocolType protocol = properties.getProtocol() != null ? properties.getProtocol() : detected.protocol();
        return new SmtpMeta(detected.host(), detected.port(), protocol, ssl, starttls);
    }

    private SendRequest buildDefaults(VeloxEmailProperties properties) {
        return SendRequest.builder()
                .from(properties.getFrom())
                .fromName(properties.getFromName())
                .replyTo(properties.getReplyTo())
                .build();
    }
}
