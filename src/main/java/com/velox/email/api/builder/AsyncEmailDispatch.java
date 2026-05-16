package com.velox.email.api.builder;

import com.velox.email.api.message.SendResponse;

import java.util.concurrent.CompletableFuture;

public interface AsyncEmailDispatch {

    CompletableFuture<SendResponse> send();
}
