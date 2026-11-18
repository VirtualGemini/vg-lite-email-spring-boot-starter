# VG Lite Email Spring Boot Starter

[English](./README.md) | 简体中文

一个独立的 Spring Boot 邮件 starter，提供链式构建、同步与异步发送、受控线程池、重试退避和可替换的发送接口。

```java
emailBuilder.to("user@example.com")
        .subject("欢迎")
        .html("<h1>你好！</h1>")
        .retry(3)
        .async()
        .send();
```

`main` 使用从 Velox 邮件模块提取的新实现。旧实现与原始提交仍保留在 [`master`](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/tree/master)。来源提交及提取过程见[迁移历史](./docs/HISTORY.md)。

## 核心能力

- 不可变链式 builder：收件人、抄送、密送、发件人、回复地址、纯文本和 HTML。
- 支持文件、Spring Resource、字节数组和流形式的附件及内联资源。
- 同步返回 `SendResponse`，异步返回 `CompletableFuture<SendResponse>`。
- 有并发上限的虚拟线程或固定平台线程池，随 Spring 容器关闭。
- 可配置重试次数、指数退避、单封邮件失败回调、拦截器和监听器。
- sender、channel、重试策略、异常转换器、builder 工厂和执行器均可替换。
- 默认禁用时保留可注入接口，不建立 SMTP 连接。
- Spring Boot 自动装配及配置元数据，不依赖 Velox 的父工程、infra 或 system 模块。

## 版本与兼容性

| 项目 | 当前分支 |
| --- | --- |
| Java | 21 及以上，编译目标为 Java 21 |
| Spring Boot | 基于 3.4.5 构建和测试 |
| 邮件 API | Jakarta Mail |
| 默认传输 | SMTP，可配置 SSL 或 STARTTLS |
| 开发版本 | `1.0.0-SNAPSHOT` |

新版与 `0.1.64` 存在不兼容变更。Java 8/11/17 或 Spring Boot 2 应用应在升级运行环境前继续使用旧版本。本构建未声明支持 Spring Boot 4。

## 快速开始

### 1. 从源码安装

`main` 上的开发版本通过源码构建：

```bash
git clone --branch main https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter.git
cd vg-lite-email-spring-boot-starter
mvn clean install
```

在 Spring Boot 3 应用中添加依赖：

```xml
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

starter 已传递引入 `spring-boot-starter-mail`，无需再单独声明。分支更新不代表新版本已经发布到 Maven Central 或 GitHub Releases。

### 2. 配置 SMTP

在 `application.yml` 中添加：

```yaml
vg:
  lite-email:
    enabled: true
    host: smtp.example.com
    port: 587
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    from: ${SMTP_USERNAME}
    from-name: 示例应用
    ssl: false
    starttls: true
    provider-auto-detect: false
```

按邮箱服务商要求提供密码或 SMTP 授权码。`from` 未设置时使用 `username`；旧的 `sender` 配置仍可在两者未设置时补充用户名和发件地址。

starter 使用 `vg.lite-email` 下的 SMTP 配置，`spring.mail.*` 不会配置这个发送器。它创建专用的 `liteEmailJavaMailSender`；如果希望默认 SMTP channel 使用自定义 JavaMailSender，请以这个 Bean 名注册。

`provider-auto-detect` 默认为 `true`，未同时设置 host/port 时会按用户名域名推断。自建和企业邮箱建议显式配置两者。内置推断表仅用于简化配置，不验证服务商当前可用性。

### 3. 发送邮件

无需添加 `@EnableAsync`，也无需扫描 starter 的包。

```java
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import org.springframework.stereotype.Service;

@Service
public class WelcomeMailService {
    private final EmailBuilder emailBuilder;

    public WelcomeMailService(EmailBuilder emailBuilder) {
        this.emailBuilder = emailBuilder;
    }

    public SendResponse sendWelcome(String recipient) {
        return emailBuilder.to(recipient)
                .subject("欢迎")
                .text("感谢加入我们。")
                .sendSync();
    }
}
```

`send()` 同样是同步发送。请检查 `response.success()`：SMTP 投递失败会返回失败结果，非法请求、扩展逻辑或执行器错误仍可能抛异常。成功表示 SMTP 服务器接受了邮件，不代表邮件已经进入收件箱。

每次 builder 调用都返回新对象，请使用链式调用或保存返回值；忽略返回值会丢失修改。注入的根 builder 可以用于构建互相独立的邮件，也可以通过 `EmailBuilderFactory<EmailBuilder>.newMessage()` 显式创建。

## 异步、附件与失败处理

```java
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

CompletableFuture<SendResponse> result = emailBuilder
        .to("user@example.com")
        .cc("copy@example.com")
        .subject("报告")
        .text("请查看附件。")
        .html("<p>请查看附件。</p><img src='cid:logo'>")
        .attachment("report.txt", "报告内容".getBytes(StandardCharsets.UTF_8), "text/plain")
        .inline("logo", "logo.png", logoBytes, "image/png") // 提供实际图片字节
        .retry(3)
        .onFailure(context -> {
            // 检查 context.response()，记录最终投递失败。
        })
        .async()
        .send();
```

重试和回调必须放在 `.async()` 之前；它返回只暴露 `send()` 的 `AsyncEmailDispatch`。异步结果既要处理失败响应，也要处理 Future 异常完成。`onFailure` 在投递重试结束后执行，不处理非法请求和执行器拒绝任务。

传入 `InputStream` 的附件重载会在构建时读取并关闭流。自定义 `InputStreamSource` 应在每次读取时提供新流，以便 SMTP 重试能重新读取附件。

## 配置参考

以下配置均位于 `vg.lite-email`：

| 配置 | 默认值 | 含义 |
| --- | --- | --- |
| `enabled` | `false` | 启用真实投递 |
| `username`、`password` | 未设置 | 默认 SMTP 实现要求的凭据 |
| `from` | `username` | 默认发件地址 |
| `from-name`、`reply-to` | 未设置 | 发件人显示名、回复地址 |
| `sender` | 未设置 | 在 username/from 缺失时补充两者 |
| `host`、`port` | 自动推断 | 显式配置时需同时设置 |
| `provider-auto-detect` | `true` | host/port 未同时设置时推断服务器 |
| `protocol` | 自动推断 | `SMTP` 或 `SMTPS` |
| `ssl`、`starttls` | 自动推断 | SSL、STARTTLS 开关 |
| `auth` | `true` | SMTP 认证开关；默认实现仍要求配置凭据 |
| `connection-timeout`、`timeout`、`write-timeout` | `5000` | 连接、读取和写入超时，单位毫秒 |
| `async.enabled` | `true` | 启用线程池；false 在调用线程执行 |
| `async.virtual-threads` | `true` | 使用虚拟线程；false 使用固定平台线程池 |
| `async.concurrency-limit` | `256` | 虚拟任务并发上限／平台线程池大小 |
| `async.thread-name-prefix` | `vg-lite-email-` | 线程名前缀 |
| `retry.enabled` | `true` | 启用重试策略 |
| `retry.default-attempts` | `1` | 未指定时的总尝试次数 |
| `retry.max-attempts` | `3` | 总尝试次数的全局上限 |
| `retry.initial-delay` | `1s` | 首次重试前的等待时间 |
| `retry.multiplier` | `2.0` | 指数退避倍率 |
| `retry.max-delay` | `30s` | 重试等待上限 |
| `logging.enabled` | `true` | 启用 starter 日志 |
| `logging.level` | `INFO` | `TRACE`、`DEBUG`、`INFO`、`WARN`、`ERROR` |

`.retry(3)` 表示最多尝试三次，包含首次发送，并受全局上限约束；`.retry()` 请求总共两次尝试。默认 SMTP channel 仅重试 `421`、`450`、`451`、`452`，永久错误和未知错误默认不重试。channel 与 retry policy 必须同时允许，才会继续尝试。

虚拟线程执行器达到并发上限后会等待许可，因此异步提交在高负载下也可能阻塞。平台线程池使用任务队列；可通过名为 `liteEmailExecutor` 的自定义 Bean 改变排队或任务接纳方式。`async.enabled: false` 时异步调用在当前线程执行并返回已完成的 Future。

`enabled` 为 false 或未配置时，默认空实现返回 `success=false`、`errorCode=-2`、`channel=NOOP`，无需凭据、重试设施或执行器。开关在容器启动时选择默认实现，不会动态重建运行中的容器；用户自定义 Bean 保持自身行为。

## 扩展接口

注册对应接口的 Spring Bean 后，默认自动装配会退让。

| `io.github.virtualgemini.vgliteemail` 下的类型 | 用途 |
| --- | --- |
| `api.sender.EmailSender` | 替换整个发送流程 |
| `spi.channel.EmailChannel` | 替换 SMTP，复用默认发送流程 |
| `spi.policy.RetryPolicy` | 自定义重试判断和等待时间 |
| `spi.policy.EmailExceptionTranslator` | 转换 sender 捕获的异常 |
| `spi.hook.EmailSendInterceptor` | 发送前修改请求、发送后修改响应 |
| `spi.hook.EmailSendListener` | 监听最终成功或失败 |
| `api.builder.EmailBuilderFactory` | 创建自定义 builder |
| 名为 `liteEmailExecutor` 的 Bean | 自定义异步执行 |

可继承 `spi.builder.AbstractEmailBuilder`、`spi.sender.AbstractEmailSender` 和 `spi.channel.AbstractEmailChannel` 来复用基础行为。SMTP channel 会自行转换邮件异常，再返回响应；替换 sender 的异常转换器不会替换这部分 SMTP 转换。

## 从 0.1.64 升级

| 旧版 | 新版 |
| --- | --- |
| Java 8+、Spring Boot 2 | Java 21+、Spring Boot 3 |
| `core.EmailBuilder` | `api.builder.EmailBuilder` |
| `core.SendRequest`／`core.SendResponse` | `api.message.SendRequest`／`api.message.SendResponse` |
| 默认启用邮件发送 | 显式设置 `vg.lite-email.enabled: true` |
| `.async().retry(3).send()` | `.retry(3).async().send()` |
| 单独引入 `spring-boot-starter-mail` | starter 已传递引入 |
| 可变构建过程 | 保存不可变 builder 的返回值 |

Maven 的 group/artifact 坐标和 `vg.lite-email` 前缀保留。升级时需要检查 API 包结构、运行环境、线程池配置及重试语义；旧 README 和实现仍在 `master` 可查。

## 开发与验证

```bash
mvn verify
```

测试覆盖启停自动装配、自定义接口、不可变构建、SMTP MIME 正文、附件、重试、回调和执行器生命周期，不连接真实 SMTP 服务器。GitHub Actions 对 `main` 的 push 和 PR 使用 Java 21、25 验证。

## 许可证与贡献

仓库保留 [Apache 2.0 许可证](./LICENSE)；提取的 Velox 邮件代码保留 [MIT 声明](./LICENSE-MIT)，并随 JAR 打包。[迁移历史](./docs/HISTORY.md)记录代码来源。

欢迎提交 Issue 或 PR。行为变更请提供可复现示例及相关测试。
