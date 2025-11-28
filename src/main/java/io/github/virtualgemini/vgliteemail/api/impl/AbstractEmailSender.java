package io.github.virtualgemini.vgliteemail.api.impl;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 18:08
 */

import io.github.virtualgemini.vgliteemail.api.IEmailChannel;
import io.github.virtualgemini.vgliteemail.api.IEmailSender;
import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 把“重试 + 线程池”做成模板方法，子类只需要实现真正的一次发送。
 */
public abstract class AbstractEmailSender implements IEmailSender {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final IEmailChannel channel;   // 供子类直接用
    private final Executor executor;
    private final RetryPolicy retryPolicy;
    private volatile int retryTimes = 1;
    protected AbstractEmailSender(IEmailChannel channel,
                                  Executor executor,
                                  RetryPolicy retryPolicy) {
        this.channel = channel;
        this.executor = executor;
        this.retryPolicy = retryPolicy;
    }

    private LiteEmailProperties properties;


    /* ---------- 开放给子类/Builder 微调 ---------- */
    public void setRetryTimes(int retryTimes) {
        this.retryTimes = Math.max(1, retryTimes);
    }

    /* ---------- 模板方法：重试逻辑 ---------- */
    @Override
    public SendResponse send(SendRequest req) {
        int max = Math.min(retryTimes, retryPolicy.getMaxRetries());
        int tried = 0;
        SendResponse resp;
        do {
            resp = tryOnce(req);          // 子类实现
            if (resp.isSuccess() || !retryable(resp.getErrorCode())) {
                break;
            }
            tried++;
            long delay = retryPolicy.nextDelay(tried);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            log.warn("第 {} 次重试，errorCode={}", tried + 1, resp.getErrorCode());
        } while (tried < max);
        return resp;
    }

    @Override
    public CompletableFuture<SendResponse> sendAsync(SendRequest req) {
        return CompletableFuture.supplyAsync(() -> send(req), executor);
    }

    /* ---------- 子类必须实现 ---------- */
    protected abstract SendResponse tryOnce(SendRequest request);

    /* ---------- 子类可覆盖：哪些错误码值得重试 ---------- */
    protected boolean retryable(int errorCode) {
        return channel.retryable(errorCode);
    }
}
