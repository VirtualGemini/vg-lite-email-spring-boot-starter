package com.velox.email.support.meta;

import com.velox.email.support.type.ProtocolType;

public record SmtpMeta(
        String host,
        int port,
        ProtocolType protocol,
        boolean ssl,
        boolean starttls
) {
}
