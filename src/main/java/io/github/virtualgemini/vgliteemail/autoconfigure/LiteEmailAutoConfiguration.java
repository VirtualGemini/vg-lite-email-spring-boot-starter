package io.github.virtualgemini.vgliteemail.autoconfigure;

import io.github.virtualgemini.vgliteemail.common.prefix.EmailPropertyPrefixes;
import io.github.virtualgemini.vgliteemail.support.channel.SmtpEmailChannel;
import io.github.virtualgemini.vgliteemail.support.meta.SmtpMeta;
import io.github.virtualgemini.vgliteemail.properties.EmailAsyncProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailLoggingProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicyProperties;
import io.github.virtualgemini.vgliteemail.core.builder.DefaultEmailBuilderFactory;
import io.github.virtualgemini.vgliteemail.core.sender.DefaultEmailExceptionTranslator;
import io.github.virtualgemini.vgliteemail.core.sender.DefaultEmailSender;
import io.github.virtualgemini.vgliteemail.core.policy.DefaultRetryPolicy;
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilderFactory;
import io.github.virtualgemini.vgliteemail.spi.policy.EmailExceptionTranslator;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendInterceptor;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendListener;
import io.github.virtualgemini.vgliteemail.spi.channel.EmailChannel;
import io.github.virtualgemini.vgliteemail.api.sender.EmailSender;
import io.github.virtualgemini.vgliteemail.spi.policy.RetryPolicy;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.common.message.EmailCommonMessages;
import io.github.virtualgemini.vgliteemail.exception.EmailConfigException;
import io.github.virtualgemini.vgliteemail.noop.DisabledEmailSender;
import io.github.virtualgemini.vgliteemail.noop.NoOpEmailChannel;
import io.github.virtualgemini.vgliteemail.support.type.ProtocolType;
import io.github.virtualgemini.vgliteemail.support.util.ManagedEmailExecutor;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
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

@AutoConfiguration(before = MailSenderAutoConfiguration.class)
@EnableConfigurationProperties({
        LiteEmailProperties.class,
        EmailAsyncProperties.class,
        RetryPolicyProperties.class,
        LiteEmailLoggingProperties.class
})
public class LiteEmailAutoConfiguration {

    private static final String SMTP_PROTOCOL = "smtp";
    private static final String UTF_8 = "UTF-8";
    private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_CONNECTION_TIMEOUT = "mail.smtp.connectiontimeout";
    private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";
    private static final String MAIL_SMTP_WRITE_TIMEOUT = "mail.smtp.writetimeout";

    @Bean(name = "liteEmailExecutor")
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
    @ConditionalOnMissingBean(name = "liteEmailExecutor")
    public ManagedEmailExecutor liteEmailExecutor(EmailAsyncProperties properties) {
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
    public LiteEmailLogger liteEmailLogger(LiteEmailLoggingProperties properties) {
        return new LiteEmailLogger(properties.isEnabled(), properties.getLevel());
    }

    @Bean
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
    @ConditionalOnMissingBean
    public RetryPolicy retryPolicy(RetryPolicyProperties properties) {
        properties.validate();
        return new DefaultRetryPolicy(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
    @ConditionalOnMissingBean
    public EmailExceptionTranslator emailExceptionTranslator() {
        return new DefaultEmailExceptionTranslator();
    }

    @Bean
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
    @ConditionalOnMissingBean(EmailSender.class)
    @ConditionalOnBean(EmailChannel.class)
    public EmailSender emailSender(EmailChannel channel,
                                    LiteEmailLogger logger,
                                    @Qualifier("liteEmailExecutor") Executor executor,
                                    RetryPolicy retryPolicy,
                                    EmailExceptionTranslator exceptionTranslator,
                                    LiteEmailProperties properties,
                                    ObjectProvider<EmailSendInterceptor> interceptorsProvider,
                                    ObjectProvider<EmailSendListener> listenersProvider) {
        List<EmailSendInterceptor> interceptors = interceptorsProvider.orderedStream().collect(Collectors.toList());
        List<EmailSendListener> listeners = listenersProvider.orderedStream().collect(Collectors.toList());
        return new DefaultEmailSender(channel, logger, executor, retryPolicy, exceptionTranslator, interceptors, listeners, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.FALSE, matchIfMissing = true)
    @ConditionalOnMissingBean(EmailChannel.class)
    public EmailChannel disabledEmailChannel(LiteEmailLogger logger) {
        return new NoOpEmailChannel(logger);
    }

    @Bean
    @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.FALSE, matchIfMissing = true)
    @ConditionalOnMissingBean(EmailSender.class)
    public EmailSender disabledEmailSender(LiteEmailLogger logger) {
        return new DisabledEmailSender(logger);
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilderFactory.class)
    @ConditionalOnBean(EmailSender.class)
    public EmailBuilderFactory<EmailBuilder> emailBuilderFactory(EmailSender emailSender, LiteEmailProperties properties) {
        return new DefaultEmailBuilderFactory(emailSender, buildDefaults(properties));
    }

    @Bean
    @ConditionalOnMissingBean(EmailBuilder.class)
    @ConditionalOnBean(EmailBuilderFactory.class)
    public EmailBuilder emailBuilder(EmailBuilderFactory<EmailBuilder> emailBuilderFactory) {
        return emailBuilderFactory.newMessage();
    }

    private SendRequest buildDefaults(LiteEmailProperties properties) {
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
        @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
        @ConditionalOnMissingBean(name = "liteEmailJavaMailSender", value = {EmailChannel.class, EmailSender.class})
        public JavaMailSender liteEmailJavaMailSender(LiteEmailProperties properties) {
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
        @ConditionalOnProperty(prefix = EmailPropertyPrefixes.EMAIL, name = EmailPropertyPrefixes.ENABLED, havingValue = EmailPropertyPrefixes.TRUE)
        @ConditionalOnMissingBean(EmailChannel.class)
        @ConditionalOnBean(name = "liteEmailJavaMailSender")
        public EmailChannel emailChannel(@Qualifier("liteEmailJavaMailSender") JavaMailSender mailSender) {
            return new SmtpEmailChannel(mailSender);
        }

        private static SmtpMeta resolveSmtpMeta(LiteEmailProperties properties) {
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
