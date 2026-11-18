package com.velox.email.api.builder;

import com.velox.email.api.builder.EmailMessageBuilder;

@Deprecated
public interface IEmailBuilder<T extends EmailMessageBuilder<T>> extends EmailMessageBuilder<T> {
}
