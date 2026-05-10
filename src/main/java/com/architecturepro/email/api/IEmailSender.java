package com.architecturepro.email.api;

import com.architecturepro.email.core.SendRequest;
import com.architecturepro.email.core.SendResponse;

import java.util.concurrent.CompletableFuture;

public interface IEmailSender {

    SendResponse send(SendRequest request);

    CompletableFuture<SendResponse> sendAsync(SendRequest request);
}
