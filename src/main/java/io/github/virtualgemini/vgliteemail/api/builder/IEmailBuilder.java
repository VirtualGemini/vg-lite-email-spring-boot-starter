package io.github.virtualgemini.vgliteemail.api.builder;

import io.github.virtualgemini.vgliteemail.api.builder.EmailMessageBuilder;

@Deprecated
public interface IEmailBuilder<T extends EmailMessageBuilder<T>> extends EmailMessageBuilder<T> {
}
