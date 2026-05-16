package com.velox.email.api;

import com.velox.email.core.EmailMessageBuilder;

@Deprecated
public interface IEmailBuilder<T extends EmailMessageBuilder<T>> extends EmailMessageBuilder<T> {
}
