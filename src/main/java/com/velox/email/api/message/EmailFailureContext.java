package com.velox.email.api.message;

public record EmailFailureContext(
        SendRequest request,
        SendResponse response,
        Throwable cause
) {
}
