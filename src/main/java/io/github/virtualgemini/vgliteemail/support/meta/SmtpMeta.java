package io.github.virtualgemini.vgliteemail.support.meta;

import io.github.virtualgemini.vgliteemail.support.type.ProtocolType;

public record SmtpMeta(
        String host,
        int port,
        ProtocolType protocol,
        boolean ssl,
        boolean starttls
) {
}
