package com.architecturepro.email.api;

public interface IEmailBuilder<T> {

    T to(String to);

    T subject(String subject);

    T text(String text);

    T html(boolean html);

    T retry(int times);

    T retry();

    T async();

    Object send();
}
