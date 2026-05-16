<h2 align="center" id="top">Velox Email Spring Boot Starter</h2>
<p align="center">一个独立、可热插拔的邮件 starter，重点提供链式发送、异步执行、重试控制与可扩展的发送契约。</p>
<div align="center"><a href="./README.md">English</a> | 简体中文</div>

<br />

## 模块概述

`velox-email-spring-boot-starter` 是一个面向 Spring Boot 应用的独立邮件模块。
它既可以开箱即用，也允许你完整替换发送契约、渠道实现、重试策略和执行方式。

这个 starter 的核心能力集中在五点：

- 链式调用式邮件构建
- 可配置线程池的异步执行
- 内建重试与退避机制
- 接口优先的扩展方式
- 基于抽象类的重写式复用

## 核心特性

### 1. 链式调用发送

模块对外提供 `EmailBuilder`，支持 builder 风格构建邮件。
收件人、主题、文本、HTML、附件、内联资源、重试提示和失败回调都可以在一次链式调用中声明完成。

示例：

```java
emailBuilder
        .to("user@example.com")
        .subject("Reset Password")
        .text("Your verification code is 123456")
        .sendSync();
```

### 2. 异步线程池执行

starter 内置一个名为 `veloxEmailExecutor` 的执行器。
支持能力包括：

- 同步兜底执行
- 虚拟线程
- 固定大小平台线程池
- 可配置并发上限
- 自定义线程名前缀

示例配置：

```yaml
velox:
  email:
    async:
      enabled: true
      virtual-threads: true
      concurrency-limit: 256
      thread-name-prefix: velox-email-
```

### 3. 重试机制

starter 内置默认重试策略实现。
支持能力包括：

- 默认尝试次数
- 最大尝试次数
- 初始延迟
- 基于倍率的退避
- 最大重试延迟

示例配置：

```yaml
velox:
  email:
    retry:
      enabled: true
      default-attempts: 1
      max-attempts: 3
      initial-delay: 1s
      multiplier: 2.0
      max-delay: 30s
```

### 4. 接口可实现

如果你只想替换发送链路中的某一部分，可以直接实现接口。

常见扩展点包括：

- `IEmailSender`
- `IEmailChannel`
- `EmailExceptionTranslator`
- `EmailSendInterceptor`
- `EmailSendListener`
- `RetryPolicy`

这使得该模块适合做 SMTP 替换、服务商适配、自定义审计以及业务级重试决策。

### 5. 抽象类可重写

如果直接实现接口过于底层，starter 也提供了可复用的抽象骨架。

可继承的基础类包括：

- `AbstractEmailBuilder`
- `AbstractEmailSender`
- `AbstractEmailChannel`

它们适合希望保留默认发送流程一部分、只重写选定行为的开发者。

## 独立可热插拔

这个模块从设计上就是隔离的，可以独立接入、独立移除。

- 不依赖 `infra` 和 `system`
- 通过 Spring Boot 自动装配启用
- 可通过 `velox.email.enabled` 开关控制
- 默认 sender、channel、retry policy、exception translator、executor 都可以替换

因此它既可以作为默认邮件 starter 使用，也可以作为你自定义邮件基础设施的契约壳存在。

## 快速开始

基础配置：

```yaml
velox:
  email:
    enabled: true
    username: xxx@example.com
    password: your-password-or-token
    from: xxx@example.com
```

HTML 异步发送示例：

```java
emailBuilder
        .to("user@example.com")
        .subject("Welcome")
        .html("<h1>Welcome to Velox</h1>")
        .async()
        .sendAsync();
```

附件与失败回调示例：

```java
emailBuilder
        .to("user@example.com")
        .subject("Report")
        .text("Please check the attachment")
        .attachment(file)
        .onFailure(context -> {
            // custom failure handling
        })
        .sendSync();
```

## 自动装配入口

- `com.velox.email.autoconfigure.VeloxEmailAutoConfiguration`

## 主要对外类型

- `com.velox.email.api.builder.EmailBuilder`
- `com.velox.email.api.builder.EmailBuilderFactory`
- `com.velox.email.api.sender.IEmailSender`
- `com.velox.email.api.channel.IEmailChannel`

## 典型使用场景

- 注册验证码
- 找回密码
- 系统通知
- 审批提醒
- 报表投递
- 可替换的产品级邮件基础设施
