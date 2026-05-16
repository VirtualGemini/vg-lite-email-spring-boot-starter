<h2 align="center" id="top">Velox Email Spring Boot Starter</h2>
<p align="center">A standalone, hot-pluggable email starter focused on fluent sending, async execution, retry control, and extensible delivery contracts.</p>
<div align="center">English | <a href="./README.zh-CN.md">简体中文</a></div>

<br />

## Overview

`velox-email-spring-boot-starter` is an independent email module for Spring Boot applications.
It is designed to be usable out of the box, while still allowing full replacement of sending contracts, channel implementations, retry strategies, and execution behavior.

This starter focuses on five core capabilities:

- fluent chained email composition
- async execution with configurable thread pools
- built-in retry strategy with backoff support
- interface-first extensibility
- abstract-class-based override points

## Core Features

### 1. Fluent Chained Sending

The module exposes `EmailBuilder` for builder-style email composition.
Recipients, subject, text, HTML, attachments, inline resources, retry hints, and failure callbacks can all be declared in one chain.

Example:

```java
emailBuilder
        .to("user@example.com")
        .subject("Reset Password")
        .text("Your verification code is 123456")
        .sendSync();
```

### 2. Async Thread Pool Execution

The starter includes a built-in executor named `veloxEmailExecutor`.
It supports:

- synchronous fallback execution
- virtual threads
- fixed platform thread pools
- configurable concurrency limits
- custom thread name prefixes

Example configuration:

```yaml
velox:
  email:
    async:
      enabled: true
      virtual-threads: true
      concurrency-limit: 256
      thread-name-prefix: velox-email-
```

### 3. Retry Mechanism

The starter includes a default retry policy implementation.
It supports:

- default attempts
- maximum attempts
- initial delay
- multiplier-based backoff
- maximum retry delay

Example configuration:

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

### 4. Interface-First Extensibility

If you want to replace only one part of the pipeline, you can implement interfaces directly.

Typical extension points include:

- `IEmailSender`
- `IEmailChannel`
- `EmailExceptionTranslator`
- `EmailSendInterceptor`
- `EmailSendListener`
- `RetryPolicy`

This makes the module suitable for SMTP replacement, provider adaptation, custom auditing, and business-specific retry decisions.

### 5. Abstract Classes for Override-Based Reuse

If direct implementation is too low-level, the starter also provides reusable abstract bases.

Available override skeletons include:

- `AbstractEmailBuilder`
- `AbstractEmailSender`
- `AbstractEmailChannel`

These are intended for developers who want to preserve part of the default pipeline while overriding selected behavior.

## Hot-Pluggable Design

This module is intentionally isolated and can be plugged in or removed independently.

- It does not depend on `infra` or `system`.
- It is activated through Spring Boot auto-configuration.
- It can be enabled or disabled with `velox.email.enabled`.
- Its default sender, channel, retry policy, exception translator, and executor can all be replaced.

This makes it suitable both as a default starter and as a contract shell for custom email infrastructure.

## Quick Start

Basic configuration:

```yaml
velox:
  email:
    enabled: true
    username: xxx@example.com
    password: your-password-or-token
    from: xxx@example.com
```

HTML async example:

```java
emailBuilder
        .to("user@example.com")
        .subject("Welcome")
        .html("<h1>Welcome to Velox</h1>")
        .async()
        .sendAsync();
```

Attachment and failure hook example:

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

## Auto-Configuration Entry

- `com.velox.email.autoconfigure.VeloxEmailAutoConfiguration`

## Primary Public Types

- `com.velox.email.api.builder.EmailBuilder`
- `com.velox.email.api.builder.EmailBuilderFactory`
- `com.velox.email.api.sender.IEmailSender`
- `com.velox.email.api.channel.IEmailChannel`

## Typical Use Cases

- registration verification
- forgot password
- system notification
- approval reminder
- report delivery
- replaceable product-level email infrastructure
