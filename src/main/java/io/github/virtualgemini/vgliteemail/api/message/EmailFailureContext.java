package io.github.virtualgemini.vgliteemail.api.message;

public record EmailFailureContext(
        SendRequest request,
        SendResponse response,
        Throwable cause
) {
}
