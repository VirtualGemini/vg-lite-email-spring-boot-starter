package com.velox.email.spi.policy;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;

import java.time.Duration;

public interface RetryPolicy {

    boolean shouldRetry(SendRequest request, SendResponse response, int attempt);

    Duration nextDelay(SendRequest request, SendResponse response, int attempt);
}
