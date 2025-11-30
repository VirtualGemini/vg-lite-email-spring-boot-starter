package io.github.virtualgemini.vgliteemail.component;

import io.github.virtualgemini.vgliteemail.channel.meta.SmtpMeta;
import io.github.virtualgemini.vgliteemail.properties.EmailAsyncProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailLoggingProperties;
import io.github.virtualgemini.vgliteemail.properties.LiteEmailProperties;
import io.github.virtualgemini.vgliteemail.properties.RetryPolicyProperties;
import io.github.virtualgemini.vgliteemail.utils.LiteMailLogUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/29 21:14
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class EmailConfigLogger implements ApplicationRunner {

    private final LiteEmailProperties liteProps;
    private final EmailAsyncProperties asyncProps;
    private final RetryPolicyProperties retryProps;
    private final LiteEmailLoggingProperties loggingProps;
    private static final long START_TIME = System.currentTimeMillis();

    private SmtpMeta smtpMeta;

    public EmailConfigLogger(LiteEmailProperties liteProps,
                             EmailAsyncProperties asyncProps,
                             RetryPolicyProperties retryProps, LiteEmailLoggingProperties loggingProps) {
        this.liteProps = liteProps;
        this.asyncProps = asyncProps;
        this.retryProps = retryProps;
        this.loggingProps = loggingProps;
    }

    /**
     * 返回 JVM 已运行毫秒数，与 Spring Boot 日志同算法
     */
    private String jvmUptime() {
        long uptime = System.currentTimeMillis() - START_TIME;
        return String.format("%.3f", uptime / 1000.0);
    }

    /*private void setDefaultValue(String protocol) {
        // SMTP
        if (protocol.equals("SMTP")) {
            smtpMeta = SmtpEmailChannel.guessMeta(liteProps.getSender());
            liteProps.setHost(smtpMeta.host());
            liteProps.setPort(smtpMeta.port());
        }
    }*/
    @Override
    public void run(ApplicationArguments args) {
        LiteMailLogUtil.printBanner();   // 先 banner
        long start = System.nanoTime();          // 开始计时
//        setDefaultValue(liteProps.getProtocol());
        LiteMailLogUtil.infoRaw("================ LiteEmailProperties =================");
        LiteMailLogUtil.infoRaw("sender: {}", liteProps.getSender());
        LiteMailLogUtil.infoRaw("password: {}", liteProps.getPassword() != null ? "*********" : null);
        LiteMailLogUtil.infoRaw("protocol: {}", liteProps.getProtocol());
        LiteMailLogUtil.infoRaw("host: {}", liteProps.getHost());
        LiteMailLogUtil.infoRaw("port: {}", liteProps.getPort());
        LiteMailLogUtil.infoRaw("ssl: {}", liteProps.isSsl());
        LiteMailLogUtil.infoRaw("connectionTimeout: {} ms", liteProps.getConnectionTimeout());
        LiteMailLogUtil.infoRaw("timeout: {} ms", liteProps.getTimeout());

        LiteMailLogUtil.infoRaw("================ RetryPolicyProperties ===============");
        LiteMailLogUtil.infoRaw("globalRetries: {}", retryProps.getGlobalRetries());
        LiteMailLogUtil.infoRaw("maxRetries: {}", retryProps.getMaxRetries());
        LiteMailLogUtil.infoRaw("initialDelay: {} ms", retryProps.getInitialDelay());

        LiteMailLogUtil.infoRaw("================ EmailAsyncProperties ================");
        LiteMailLogUtil.infoRaw("corePoolSize: {}", asyncProps.getCorePoolSize());
        LiteMailLogUtil.infoRaw("maxPoolSize: {}", asyncProps.getMaxPoolSize());
        LiteMailLogUtil.infoRaw("queueCapacity: {}", asyncProps.getQueueCapacity());
        LiteMailLogUtil.infoRaw("keepAliveSeconds: {}", asyncProps.getKeepAliveSeconds());
        LiteMailLogUtil.infoRaw("rejectedPolicy: {}", asyncProps.getRejectedPolicy());
        LiteMailLogUtil.infoRaw("awaitTerminationSeconds: {}", asyncProps.getAwaitTerminationSeconds());
        LiteMailLogUtil.infoRaw("threadNamePrefix: {}", asyncProps.getThreadNamePrefix());
        LiteMailLogUtil.infoRaw("======================================================");

        /* ===== 收官耗时 ===== */
        long costMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        LiteMailLogUtil.infoRaw("LiteEmail Initialized in {} ms (JVM running for {} s)", costMs, jvmUptime());

    }
}
