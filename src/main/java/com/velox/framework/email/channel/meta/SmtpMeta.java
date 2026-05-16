package com.velox.email.channel.meta;

import com.velox.email.enums.ProtocolType;

public record SmtpMeta(
        String host,
        int port,
        ProtocolType protocol,
        boolean ssl,
        boolean starttls
) {
}
