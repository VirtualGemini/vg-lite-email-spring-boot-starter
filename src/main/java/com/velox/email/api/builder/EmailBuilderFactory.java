package com.velox.email.api.builder;

import com.velox.email.api.message.SendResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public interface EmailBuilderFactory<T extends EmailMessageBuilder<T>> {

    T newMessage();

    default SendResponse send(UnaryOperator<T> customizer) {
        return customizer.apply(newMessage()).sendSync();
    }

    default CompletableFuture<SendResponse> sendAsync(UnaryOperator<T> customizer) {
        return customizer.apply(newMessage()).sendAsync();
    }
}
