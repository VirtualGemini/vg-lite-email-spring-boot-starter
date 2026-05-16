package com.velox.email.core;

import java.util.Set;

public interface EmailChannel {

    Set<Integer> DEFAULT_RETRYABLE = Set.of(421, 450, 451, 452);

    String name();

    SendResponse send(SendRequest request);

    default boolean retryable(SendResponse response) {
        return DEFAULT_RETRYABLE.contains(response.errorCode());
    }
}
