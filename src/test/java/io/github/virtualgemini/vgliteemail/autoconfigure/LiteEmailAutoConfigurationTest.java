package io.github.virtualgemini.vgliteemail.autoconfigure;

import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilderFactory;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.api.sender.EmailSender;
import io.github.virtualgemini.vgliteemail.common.error.EmailErrorCode;
import io.github.virtualgemini.vgliteemail.noop.DisabledEmailSender;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.spi.channel.EmailChannel;
import io.github.virtualgemini.vgliteemail.support.util.ManagedEmailExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

class LiteEmailAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(LiteEmailAutoConfiguration.class));

    @Test
    void disabledByDefaultKeepsBuilderAvailableWithoutCredentialsOrExecutor() {
        runner.run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(EmailBuilder.class)
                    .hasSingleBean(EmailBuilderFactory.class).hasSingleBean(EmailChannel.class)
                    .hasSingleBean(EmailSender.class).doesNotHaveBean(JavaMailSender.class)
                    .doesNotHaveBean(ManagedEmailExecutor.class);
            assertThat(context.getBean(EmailSender.class)).isInstanceOf(DisabledEmailSender.class);
            SendResponse response = context.getBean(EmailBuilder.class).to("user@example.com").send();
            assertThat(response.success()).isFalse();
            assertThat(response.errorCode()).isEqualTo(EmailErrorCode.DISABLED.code());
            assertThat(context.getBean(EmailBuilder.class).async().send().join()).isEqualTo(response);
        });
    }

    @Test
    void enabledSmtpBindsVgConfigurationAndCreatesCompletePipeline() {
        smtpRunner().withPropertyValues("vg.lite-email.async.virtual-threads=false")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(EmailBuilder.class)
                            .hasSingleBean(EmailChannel.class).hasSingleBean(EmailSender.class)
                            .hasSingleBean(JavaMailSender.class).hasBean("liteEmailExecutor");
                    JavaMailSenderImpl mail = (JavaMailSenderImpl) context.getBean(JavaMailSender.class);
                    assertThat(mail.getHost()).isEqualTo("smtp.example.com");
                    assertThat(mail.getPort()).isEqualTo(587);
                    assertThat(mail.getUsername()).isEqualTo("sender@example.com");
                    assertThat(mail.getJavaMailProperties()).containsEntry("mail.smtp.starttls.enable", "true");
                    assertThat(context.getBean(EmailBuilder.class).build().from()).isEqualTo("sender@example.com");
                });
    }

    @Test
    void legacySenderPropertyStillSuppliesUsernameAndFrom() {
        runner.withPropertyValues("vg.lite-email.enabled=true", "vg.lite-email.sender=sender@qq.com",
                "vg.lite-email.password=token").run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(LiteEmailProperties.class).getUsername()).isEqualTo("sender@qq.com");
            assertThat(context.getBean(EmailBuilder.class).build().from()).isEqualTo("sender@qq.com");
            assertThat(((JavaMailSenderImpl) context.getBean(JavaMailSender.class)).getHost()).isEqualTo("smtp.qq.com");
        });
    }

    @Test
    void customChannelUsesDefaultPipelineWithoutSmtpCredentials() {
        EmailChannel channel = new TestChannel();
        runner.withPropertyValues("vg.lite-email.enabled=true", "vg.lite-email.from=sender@example.com")
                .withBean(EmailChannel.class, () -> channel).run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(EmailChannel.class)
                            .hasSingleBean(EmailSender.class).doesNotHaveBean(JavaMailSender.class);
                    SendResponse response = context.getBean(EmailBuilder.class).to("user@example.com").send();
                    assertThat(response.success()).isTrue();
                    assertThat(response.channel()).isEqualTo("test");
                });
    }

    @Test
    void customSenderReplacesDefaultAndDoesNotRequireSmtpCredentials() {
        EmailSender sender = new EmailSender() {
            @Override
            public SendResponse send(SendRequest request) {
                return SendResponse.builder().success(true).channel("custom").build();
            }

            @Override
            public CompletableFuture<SendResponse> sendAsync(SendRequest request) {
                return CompletableFuture.completedFuture(send(request));
            }
        };
        runner.withPropertyValues("vg.lite-email.enabled=true")
                .withBean(EmailSender.class, () -> sender).run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(EmailSender.class)
                            .hasSingleBean(EmailBuilder.class).doesNotHaveBean(JavaMailSender.class);
                    assertThat(context.getBean(EmailBuilder.class).send().channel()).isEqualTo("custom");
                });
    }

    @Test
    void disabledConfigurationAlsoRespectsCanonicalCustomContracts() {
        runner.withBean(EmailChannel.class, TestChannel::new).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(EmailChannel.class);
            assertThat(context.getBean(EmailChannel.class)).isInstanceOf(TestChannel.class);
        });
        EmailSender sender = new DisabledEmailSender(new io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger(
                false, io.github.virtualgemini.vgliteemail.support.type.LogLevelType.INFO));
        runner.withBean(EmailSender.class, () -> sender).run(context -> {
            assertThat(context).hasNotFailed().hasSingleBean(EmailSender.class).hasSingleBean(EmailBuilder.class);
            assertThat(context.getBean(EmailSender.class)).isSameAs(sender);
        });
    }

    @Test
    void namedCustomExecutorIsUsedForAsyncDispatch() {
        int[] calls = {0};
        Executor executor = command -> {
            calls[0]++;
            command.run();
        };
        runner.withPropertyValues("vg.lite-email.enabled=true", "vg.lite-email.from=sender@example.com")
                .withBean(EmailChannel.class, TestChannel::new)
                .withBean("liteEmailExecutor", Executor.class, () -> executor).run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(EmailBuilder.class).to("user@example.com").async().send().join().success()).isTrue();
                    assertThat(calls[0]).isEqualTo(1);
                });
    }

    @Test
    void missingSmtpCredentialsFailAtStartupWhenEnabled() {
        runner.withPropertyValues("vg.lite-email.enabled=true").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).hasStackTraceContaining("vg.lite-email.username must not be blank");
        });
    }

    @Test
    void bootMailConfigurationCoexistsWithStarter() {
        smtpRunner().withConfiguration(AutoConfigurations.of(MailSenderAutoConfiguration.class))
                .withPropertyValues("spring.mail.host=another.example.com").run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(EmailSender.class).hasSingleBean(JavaMailSender.class);
                    assertThat(context.getBean("liteEmailJavaMailSender", JavaMailSenderImpl.class).getHost())
                            .isEqualTo("smtp.example.com");
                });
    }

    private ApplicationContextRunner smtpRunner() {
        return runner.withPropertyValues("vg.lite-email.enabled=true", "vg.lite-email.host=smtp.example.com",
                "vg.lite-email.port=587", "vg.lite-email.username=sender@example.com",
                "vg.lite-email.password=token", "vg.lite-email.provider-auto-detect=false");
    }

    private static class TestChannel implements EmailChannel {
        @Override
        public String name() {
            return "test";
        }

        @Override
        public SendResponse send(SendRequest request) {
            return SendResponse.builder().success(true).build();
        }
    }
}
