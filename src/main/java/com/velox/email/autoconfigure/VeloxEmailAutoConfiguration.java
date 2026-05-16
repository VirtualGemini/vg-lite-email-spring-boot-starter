package com.velox.email.autoconfigure;

import com.velox.email.api.channel.IEmailChannel;
import com.velox.email.api.sender.IEmailSender;
import com.velox.email.support.channel.SmtpEmailChannel;
import com.velox.email.support.meta.SmtpMeta;
import com.velox.email.properties.EmailAsyncProperties;
import com.velox.email.properties.VeloxEmailLoggingProperties;
import com.velox.email.properties.VeloxEmailProperties;
import com.velox.email.properties.RetryPolicyProperties;
import com.velox.email.core.builder.DefaultEmailBuilderFactory;
import com.velox.email.core.sender.DefaultEmailExceptionTranslator;
import com.velox.email.core.sender.DefaultEmailSender;
import com.velox.email.core.policy.DefaultRetryPolicy;
import com.velox.email.api.builder.EmailBuilder;
import com.velox.email.api.builder.EmailBuilderFactory;
import com.velox.email.spi.policy.EmailExceptionTranslator;
import com.velox.email.spi.hook.EmailSendInterceptor;
import com.velox.email.spi.hook.EmailSendListener;
import com.velox.email.spi.channel.EmailChannel;
import com.velox.email.api.sender.EmailSender;
import com.velox.email.spi.policy.RetryPolicy;
import com.velox.email.api.message.SendRequest;
import com.velox.email.common.message.EmailCommonMessages;
import com.velox.email.exception.EmailConfigException;
import com.velox.email.noop.DisabledEmailSender;
import com.velox.email.noop.NoOpEmailChannel;
import com.velox.email.support.type.ProtocolType;
import com.velox.email.support.util.ManagedEmailExecutor;
import com.velox.email.support.util.VeloxEmailLogger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.concurrent.Executor;

@AutoConfiguration
@EnableConfigurationProperties({
        VeloxEmailProperties.class,
        EmailAsyncProperties.class,
        RetryPolicyProperties.class,
        VeloxEmailLoggingProperties.class
})
public class VeloxEmailAutoConfiguration {

    private static final String SMTP_PROTOCOL = "smtp";
    private static final String UTF_8 = "UTF-8";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
    private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
    private static final String MAIL_SMTP_WRITE_TIMEOUT = "mail.smtp.writetimeout";

    @Bean(name = "veloxEmailExecutor")
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(name = "veloxEmailExecutor")
    public ManagedEmailExecutor veloxEmailExecutor(EmailAsyncProperties properties) {
        properties.validate();
        if (!properties.isEnabled()) {
            return ManagedEmailExecutor.direct();
        }
        if (properties.isVirtualThreads()) {
            return ManagedEmailExecutor.boundedVirtualThreads(
                    properties.getConcurrencyLimit(),
                    properties.getThreadNamePrefix()
            );
        }
        return ManagedEmailExecutor.fixedThreadPool(
                properties.getConcurrencyLimit(),
                properties.getThreadNamePrefix()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public VeloxEmailLogger veloxEmailLogger(VeloxEmailLoggingProperties properties) {
        return new VeloxEmailLogger(properties.isEnabled(), properties.getLevel());
    }

    @Bean
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy(RetryPolicyProperties properties) {
        properties.validate();
        return new DefaultRetryPolicy(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    public EmailExceptionTranslator emailExceptionTranslator() {
        return new DefaultEmailExceptionTranslator();
    }

    @Bean
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean
    @ConditionalOnBean(EmailChannel.class)
    public IEmailSender emailSender(EmailChannel channel,
                                    VeloxEmailLogger logger,
                                    @Qualifier("veloxEmailExecutor") Executor executor,
                                    RetryPolicy retryPolicy,
                                    EmailExceptionTranslator exceptionTranslator,
                                    VeloxEmailProperties properties,
                                    ObjectProvider<EmailSendInterceptor> interceptorsProvider,
                                    ObjectProvider<EmailSendListener> listenersProvider) {
        List<EmailSendInterceptor> interceptors = interceptorsProvider.orderedStream().collect(Collectors.toList());
        List<EmailSendListener> listeners = listenersProvider.orderedStream().collect(Collectors.toList());
        return new DefaultEmailSender(channel, logger, executor, retryPolicy, exceptionTranslator, interceptors, listeners, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnMissingBean(IEmailChannel.class)
    public IEmailChannel disabledEmailChannel(VeloxEmailLogger logger) {
        return new NoOpEmailChannel(logger);
    }

    @Bean
    @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnMissingBean(IEmailSender.class)
    public IEmailSender disabledEmailSender(VeloxEmailLogger logger) {
        return new DisabledEmailSender(logger);
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilderFactory.class)
    @ConditionalOnBean(EmailSender.class)
    public EmailBuilderFactory<EmailBuilder> emailBuilderFactory(EmailSender emailSender, VeloxEmailProperties properties) {
        return new DefaultEmailBuilderFactory(emailSender, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilder.class)
    @ConditionalOnBean(EmailBuilderFactory.class)
    public EmailBuilder emailBuilder(EmailBuilderFactory<EmailBuilder> emailBuilderFactory) {
        return emailBuilderFactory.newMessage();
    }

    private SendRequest buildDefaults(VeloxEmailProperties properties) {
        return SendRequest.builder()
                .from(properties.getFrom())
                .fromName(properties.getFromName())
                .replyTo(properties.getReplyTo())
                .build();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(JavaMailSender.class)
    static class SmtpSupportConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
        @ConditionalOnMissingBean(name = "veloxEmailJavaMailSender", value = {EmailChannel.class, EmailSender.class})
        public JavaMailSender veloxEmailJavaMailSender(VeloxEmailProperties properties) {
            properties.validateForSmtp();

            SmtpMeta meta = resolveSmtpMeta(properties);
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(meta.host());
            sender.setPort(meta.port());
            sender.setProtocol(SMTP_PROTOCOL);
            sender.setUsername(properties.getUsername());
            sender.setPassword(properties.getPassword());
            sender.setDefaultEncoding(UTF_8);

            Properties mailProperties = sender.getJavaMailProperties();
            mailProperties.setProperty(MAIL_TRANSPORT_PROTOCOL, sender.getProtocol());
            mailProperties.setProperty(MAIL_SMTP_AUTH, Boolean.toString(properties.isAuth()));
            mailProperties.setProperty(MAIL_SMTP_SSL_ENABLE, Boolean.toString(meta.ssl()));
            mailProperties.setProperty(MAIL_SMTP_STARTTLS_ENABLE, Boolean.toString(meta.starttls()));
            mailProperties.setProperty(MAIL_SMTP_CONNECTION_TIMEOUT, Long.toString(properties.getConnectionTimeout()));
            mailProperties.setProperty(MAIL_SMTP_TIMEOUT, Long.toString(properties.getTimeout()));
            mailProperties.setProperty(MAIL_SMTP_WRITE_TIMEOUT, Long.toString(properties.getWriteTimeout()));
            return sender;
        }

        @Bean
        @ConditionalOnProperty(prefix = "velox.email", name = "enabled", havingValue = "true")
        @ConditionalOnMissingBean
        @ConditionalOnBean(name = "veloxEmailJavaMailSender")
        public IEmailChannel emailChannel(@Qualifier("veloxEmailJavaMailSender") JavaMailSender mailSender) {
            return new SmtpEmailChannel(mailSender);
        }

        private static SmtpMeta resolveSmtpMeta(VeloxEmailProperties properties) {
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
                throw new EmailConfigException(EmailCommonMessages.EMAIL_HOST_AND_PORT_REQUIRED);
            }

            SmtpMeta detected = SmtpEmailChannel.guessMeta(properties.getUsername());
            boolean ssl = properties.getSsl() != null ? properties.getSsl() : detected.ssl();
            boolean starttls = properties.getStarttls() != null ? properties.getStarttls() : detected.starttls();
            ProtocolType protocol = properties.getProtocol() != null ? properties.getProtocol() : detected.protocol();
            return new SmtpMeta(detected.host(), detected.port(), protocol, ssl, starttls);
        }
    }
}
