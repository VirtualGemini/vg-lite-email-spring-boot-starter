package com.velox.email.api.sender;

import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;

import java.util.concurrent.CompletableFuture;

public interface EmailSender {

    SendResponse send(SendRequest request);

    CompletableFuture<SendResponse> sendAsync(SendRequest request);
}
