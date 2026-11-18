package io.github.virtualgemini.vgliteemail.core.sender;

import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.message.EmailFailureContext;
import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import io.github.virtualgemini.vgliteemail.core.policy.DefaultRetryPolicy;
import io.github.virtualgemini.vgliteemail.exception.EmailSendException;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicyProperties;
import io.github.virtualgemini.vgliteemail.spi.channel.EmailChannel;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendInterceptor;
import io.github.virtualgemini.vgliteemail.spi.hook.EmailSendListener;
import io.github.virtualgemini.vgliteemail.support.type.LogLevelType;
import io.github.virtualgemini.vgliteemail.support.util.LiteEmailLogger;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailDeliveryTest {

    @Test
    void immutableBuilderKeepsMessagesAndRecipientsIndependent() {
        EmailBuilder root = builder(new ScriptedChannel(0), new RetryPolicyProperties(), List.of(), List.of());
        EmailBuilder first = root.to("first@example.com").subject("first");
        EmailBuilder second = root.to("second@example.com").subject("second");
        assertThat(root.build().to()).isEmpty();
        assertThat(first.build().to()).containsExactly("first@example.com");
        assertThat(first.build().subject()).isEqualTo("first");
        assertThat(second.build().to()).containsExactly("second@example.com");
        assertThatThrownBy(() -> first.build().to().add("other@example.com")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void transientFailureRetriesUntilSuccessAndNotifiesOnce() {
        ScriptedChannel channel = new ScriptedChannel(2);
        List<String> events = new ArrayList<>();
        EmailSendInterceptor interceptor = new EmailSendInterceptor() {
            @Override
            public SendRequest beforeSend(SendRequest request) {
                events.add("before");
                return request.toBuilder().subject("intercepted").build();
            }

            @Override
            public SendResponse afterSend(SendRequest request, SendResponse response) {
                events.add("after");
                return response;
            }
        };
        EmailSendListener listener = new EmailSendListener() {
            @Override
            public void onSuccess(SendRequest request, SendResponse response) {
                events.add("success");
            }
        };
        SendResponse response = builder(channel, immediateRetry(), List.of(interceptor), List.of(listener))
                .to("user@example.com").retry(3).send();
        assertThat(response.success()).isTrue();
        assertThat(response.attempts()).isEqualTo(3);
        assertThat(channel.requests).hasSize(3).allSatisfy(request -> {
            assertThat(request.from()).isEqualTo("sender@example.com");
            assertThat(request.subject()).isEqualTo("intercepted");
        });
        assertThat(events).containsExactly("before", "after", "success");
    }

    @Test
    void exhaustedRetriesRespectGlobalLimitAndInvokeFailureHookOnce() {
        ScriptedChannel channel = new ScriptedChannel(10);
        AtomicReference<EmailFailureContext> failure = new AtomicReference<>();
        SendResponse response = builder(channel, immediateRetry(), List.of(), List.of())
                .to("user@example.com").retry(20).onFailure(failure::set).send();
        assertThat(response.success()).isFalse();
        assertThat(response.attempts()).isEqualTo(3);
        assertThat(channel.requests).hasSize(3);
        assertThat(failure.get().response()).isEqualTo(response);
    }

    @Test
    void disabledRetryAndDefaultAttemptsSendOnlyOnce() {
        ScriptedChannel defaultChannel = new ScriptedChannel(10);
        assertThat(builder(defaultChannel, immediateRetry(), List.of(), List.of())
                .to("user@example.com").send().attempts()).isEqualTo(1);
        RetryPolicyProperties properties = immediateRetry();
        properties.setEnabled(false);
        ScriptedChannel disabledRetryChannel = new ScriptedChannel(10);
        assertThat(builder(disabledRetryChannel, properties, List.of(), List.of())
                .to("user@example.com").retry(3).send().attempts()).isEqualTo(1);
    }

    @Test
    void exponentialBackoffIsBoundedByMaximumDelay() {
        RetryPolicyProperties properties = new RetryPolicyProperties();
        DefaultRetryPolicy policy = new DefaultRetryPolicy(properties);
        SendRequest request = SendRequest.builder().build();
        SendResponse response = SendResponse.builder().errorCode(451).build();
        assertThat(policy.nextDelay(request, response, 1)).isEqualTo(Duration.ofSeconds(1));
        assertThat(policy.nextDelay(request, response, 2)).isEqualTo(Duration.ofSeconds(2));
        assertThat(policy.nextDelay(request, response, 10)).isEqualTo(Duration.ofSeconds(30));
    }

    @Test
    void invalidRequestFailsBeforeCallingChannel() {
        ScriptedChannel channel = new ScriptedChannel(0);
        assertThatThrownBy(() -> builder(channel, immediateRetry(), List.of(), List.of()).send())
                .isInstanceOf(EmailSendException.class);
        assertThat(channel.requests).isEmpty();
    }

    private RetryPolicyProperties immediateRetry() {
        RetryPolicyProperties properties = new RetryPolicyProperties();
        properties.setInitialDelay(Duration.ZERO);
        return properties;
    }

    private EmailBuilder builder(EmailChannel channel, RetryPolicyProperties retry,
                                 List<EmailSendInterceptor> interceptors, List<EmailSendListener> listeners) {
        return new EmailBuilder(new DefaultEmailSender(channel, new LiteEmailLogger(false, LogLevelType.INFO),
                Runnable::run, new DefaultRetryPolicy(retry), new DefaultEmailExceptionTranslator(), interceptors, listeners,
                SendRequest.builder().from("sender@example.com").build()));
    }

    private static class ScriptedChannel implements EmailChannel {
        private final int failures;
        private final List<SendRequest> requests = new ArrayList<>();

        private ScriptedChannel(int failures) {
            this.failures = failures;
        }

        @Override
        public String name() {
            return "scripted";
        }

        @Override
        public SendResponse send(SendRequest request) {
            requests.add(request);
            return SendResponse.builder().success(requests.size() > failures).errorCode(451).build();
        }
    }
}
