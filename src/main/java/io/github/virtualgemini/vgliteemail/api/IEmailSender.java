package io.github.virtualgemini.vgliteemail.api;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:04
 */

import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 最低门槛：任何组件只要能实现这个接口，就被认为“可以发邮件”。
 */
public interface IEmailSender {

    /**
     * 同步发送
     */
    SendResponse send(SendRequest request);

    /**
     * 异步发送
     */
    CompletableFuture<SendResponse> sendAsync(SendRequest request);
}