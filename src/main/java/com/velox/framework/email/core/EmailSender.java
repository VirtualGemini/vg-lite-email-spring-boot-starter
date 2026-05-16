package com.velox.email.core;

import java.util.concurrent.CompletableFuture;

public interface EmailSender {

    SendResponse send(SendRequest request);

    CompletableFuture<SendResponse> sendAsync(SendRequest request);
}
