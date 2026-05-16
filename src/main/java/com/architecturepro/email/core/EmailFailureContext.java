package com.architecturepro.email.core;

public record EmailFailureContext(
        SendRequest request,
        SendResponse response,
        Throwable cause
) {
}
