package io.github.virtualgemini.vgliteemail.api;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:12
 */
public interface IEmailBuilder<T> {
    T to(String to);
    T subject(String subject);
    T text(String text);
    T html(boolean html);
    T retry(int times);
    T retry();
    T async();
    Object send();          // 返回类型由实现类决定
}
