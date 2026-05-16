package com.architecturepro.email.autoconfigure;

import com.architecturepro.email.api.IEmailChannel;
import com.architecturepro.email.api.IEmailSender;
import com.architecturepro.email.channel.impl.SmtpEmailChannel;
import com.architecturepro.email.channel.meta.SmtpMeta;
import com.architecturepro.email.config.properties.EmailAsyncProperties;
import com.architecturepro.email.config.properties.LiteEmailLoggingProperties;
import com.architecturepro.email.config.properties.LiteEmailProperties;
import com.architecturepro.email.config.properties.RetryPolicyProperties;
import com.architecturepro.email.core.DefaultEmailBuilderFactory;
import com.architecturepro.email.core.DefaultEmailExceptionTranslator;
import com.architecturepro.email.core.DefaultEmailSender;
import com.architecturepro.email.core.DefaultRetryPolicy;
import com.architecturepro.email.core.EmailBuilder;
import com.architecturepro.email.core.EmailBuilderFactory;
import com.architecturepro.email.core.EmailExceptionTranslator;
import com.architecturepro.email.core.EmailSendInterceptor;
import com.architecturepro.email.core.EmailSendListener;
import com.architecturepro.email.core.EmailChannel;
import com.architecturepro.email.core.EmailSender;
import com.architecturepro.email.core.RetryPolicy;
import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.enums.ProtocolType;
import com.architecturepro.email.util.LiteMailLogUtil;
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
@ConditionalOnProperty(prefix = "architecture.email", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({
        LiteEmailProperties.class,
        EmailAsyncProperties.class,
        RetryPolicyProperties.class,
        LiteEmailLoggingProperties.class
})
public class ArchitectureEmailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "architectureEmailJavaMailSender", value = {EmailChannel.class, EmailSender.class})
    public JavaMailSender architectureEmailJavaMailSender(LiteEmailProperties properties,
                                                          LiteEmailLoggingProperties loggingProperties) {
        LiteMailLogUtil.setLoggingProperties(loggingProperties);
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

    @Bean(name = "architectureEmailExecutor")
    @ConditionalOnMissingBean(name = "architectureEmailExecutor")
    public Executor architectureEmailExecutor(EmailAsyncProperties properties) {
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
    @ConditionalOnBean(name = "architectureEmailJavaMailSender")
    public IEmailChannel emailChannel(@Qualifier("architectureEmailJavaMailSender") JavaMailSender mailSender) {
        return new SmtpEmailChannel(mailSender);
    }

    @Bean
    @ConditionalOnMissingBean
    public IEmailSender emailSender(EmailChannel channel,
                                    @Qualifier("architectureEmailExecutor") Executor executor,
                                    RetryPolicy retryPolicy,
                                    EmailExceptionTranslator exceptionTranslator,
                                    LiteEmailProperties properties,
                                    ObjectProvider<EmailSendInterceptor> interceptorsProvider,
                                    ObjectProvider<EmailSendListener> listenersProvider) {
        List<EmailSendInterceptor> interceptors = interceptorsProvider.orderedStream().collect(Collectors.toList());
        List<EmailSendListener> listeners = listenersProvider.orderedStream().collect(Collectors.toList());
        return new DefaultEmailSender(channel, executor, retryPolicy, exceptionTranslator, interceptors, listeners, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilderFactory.class)
    public EmailBuilderFactory<EmailBuilder> emailBuilderFactory(EmailSender emailSender, LiteEmailProperties properties) {
        return new DefaultEmailBuilderFactory(emailSender, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilder.class)
    @ConditionalOnBean(DefaultEmailBuilderFactory.class)
    public EmailBuilder emailBuilder(DefaultEmailBuilderFactory emailBuilderFactory) {
        return emailBuilderFactory.newMessage();
    }

    private SmtpMeta resolveSmtpMeta(LiteEmailProperties properties) {
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
            throw new IllegalArgumentException("architecture.email.host and architecture.email.port must be configured when provider-auto-detect=false");
        }

        SmtpMeta detected = SmtpEmailChannel.guessMeta(properties.getUsername());
        boolean ssl = properties.getSsl() != null ? properties.getSsl() : detected.ssl();
        boolean starttls = properties.getStarttls() != null ? properties.getStarttls() : detected.starttls();
        ProtocolType protocol = properties.getProtocol() != null ? properties.getProtocol() : detected.protocol();
        return new SmtpMeta(detected.host(), detected.port(), protocol, ssl, starttls);
    }

    private SendRequest buildDefaults(LiteEmailProperties properties) {
        return SendRequest.builder()
                .from(properties.getFrom())
                .fromName(properties.getFromName())
                .replyTo(properties.getReplyTo())
                .build();
    }
}
