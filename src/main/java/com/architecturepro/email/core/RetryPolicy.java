package com.architecturepro.email.core;

import java.time.Duration;

public interface RetryPolicy {

    boolean shouldRetry(SendRequest request, SendResponse response, int attempt);

    Duration nextDelay(SendRequest request, SendResponse response, int attempt);
}
