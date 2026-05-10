package com.architecturepro.email.api;

import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;

import java.util.Set;

public interface IEmailChannel {

    Set<Integer> DEFAULT_RETRYABLE = Set.of(421, 451, 452);

    String name();

    SendResponse send(SendRequest request);

    default boolean retryable(int errorCode) {
        return DEFAULT_RETRYABLE.contains(errorCode);
    }
}
