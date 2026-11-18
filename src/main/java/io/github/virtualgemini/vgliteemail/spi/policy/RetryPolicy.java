package io.github.virtualgemini.vgliteemail.spi.policy;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

import java.time.Duration;

public interface RetryPolicy {

    boolean shouldRetry(SendRequest request, SendResponse response, int attempt);

    Duration nextDelay(SendRequest request, SendResponse response, int attempt);
}
