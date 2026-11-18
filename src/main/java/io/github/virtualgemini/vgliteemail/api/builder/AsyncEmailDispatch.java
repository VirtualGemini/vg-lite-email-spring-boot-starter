package io.github.virtualgemini.vgliteemail.api.builder;

import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

import java.util.concurrent.CompletableFuture;

public interface AsyncEmailDispatch {

    CompletableFuture<SendResponse> send();
}
