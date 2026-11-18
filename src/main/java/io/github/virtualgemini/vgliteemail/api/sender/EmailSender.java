package io.github.virtualgemini.vgliteemail.api.sender;

import io.github.virtualgemini.vgliteemail.api.message.SendRequest;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

import java.util.concurrent.CompletableFuture;

public interface EmailSender {

    SendResponse send(SendRequest request);

    CompletableFuture<SendResponse> sendAsync(SendRequest request);
}
