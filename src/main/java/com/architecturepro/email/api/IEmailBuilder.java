package com.architecturepro.email.api;

import com.architecturepro.email.core.EmailMessageBuilder;

@Deprecated
public interface IEmailBuilder<T extends EmailMessageBuilder<T>> extends EmailMessageBuilder<T> {
}
