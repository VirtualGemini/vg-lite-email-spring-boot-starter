# VG Lite Email Spring Boot Starter

English | [简体中文](./README.zh-CN.md)

A standalone email starter for Spring Boot, with fluent message composition, synchronous and asynchronous delivery, bounded executors, retry with backoff, and replaceable sending contracts.

```java
emailBuilder.to("user@example.com")
        .subject("Welcome")
        .html("<h1>Hello!</h1>")
        .retry(3)
        .async()
        .send();
```

`main` contains the new implementation extracted from the Velox email module. The previous implementation and its original commits remain on [`master`](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/tree/master). See [migration history](./docs/HISTORY.md) for source commits and extraction details.

## Features

- Immutable fluent builders for To, CC, BCC, sender identity, reply address, plain text, and HTML.
- Attachments and inline resources from files, Spring resources, byte arrays, and streams.
- Synchronous `SendResponse` and asynchronous `CompletableFuture<SendResponse>` results.
- Bounded virtual threads or fixed platform thread pools with managed shutdown.
- Configurable retry attempts, exponential backoff, per-message failure callbacks, interceptors, and listeners.
- Replaceable sender, channel, retry policy, exception translator, builder factory, and executor.
- Disabled mode that keeps injectable contracts available without opening an SMTP connection.
- Spring Boot auto-configuration and generated configuration metadata, without any Velox framework dependency.

## Compatibility

| Item | This branch |
| --- | --- |
| Java | 21 or later; bytecode targets Java 21 |
| Spring Boot | Built and tested against 3.4.5 |
| Mail API | Jakarta Mail |
| Default transport | SMTP, with SSL or STARTTLS |
| Development version | `1.0.0-SNAPSHOT` |

The new API is a breaking change from `0.1.64`. Java 8/11/17 and Spring Boot 2 applications should continue using the old version until upgraded. Spring Boot 4 compatibility is not claimed by this build.

## Quick start

### 1. Build and install

The development version on `main` is built from source:

```bash
git clone --branch main https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter.git
cd vg-lite-email-spring-boot-starter
mvn clean install
```

Add it to a Spring Boot 3 application:

```xml
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

`spring-boot-starter-mail` is included transitively. A separate mail dependency is no longer required. This branch does not imply that a new release has been published to Maven Central or GitHub Releases.

### 2. Configure SMTP

Add to `application.yml`:

```yaml
vg:
  lite-email:
    enabled: true
    host: smtp.example.com
    port: 587
    username: ${SMTP_USERNAME}
    password: ${SMTP_PASSWORD}
    from: ${SMTP_USERNAME}
    from-name: Example App
    ssl: false
    starttls: true
    provider-auto-detect: false
```

Use the credentials or authorization token required by your SMTP provider. `from` falls back to `username`. The legacy `sender` property still supplies both values when they are unset.

The starter owns its SMTP configuration under `vg.lite-email`; `spring.mail.*` does not configure this sender. It creates a dedicated `liteEmailJavaMailSender`. To supply your own JavaMailSender for the default SMTP channel, register it with that bean name.

With `provider-auto-detect: true` (the default), omitted host/port are inferred from the username domain. Explicitly configuring both host and port is preferable for custom or enterprise servers. The inference table is a built-in convenience and does not verify provider availability.

### 3. Send a message

No `@EnableAsync` or component scan of the starter package is needed.

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
                .subject("Welcome")
                .text("Thanks for joining us.")
                .sendSync();
    }
}
```

`send()` is also synchronous. Check `response.success()`; a completed SMTP call can return a failed response instead of throwing. Invalid requests and extension/executor errors can throw. Success indicates acceptance by the SMTP server, not delivery to the recipient's inbox.

Every builder method returns a new builder. Chain methods or retain the returned value; ignoring it discards the change. An injected root builder can be reused for independent messages. `EmailBuilderFactory<EmailBuilder>.newMessage()` is available when explicit message creation is useful.

## Async, attachments, and failure handling

```java
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;

CompletableFuture<SendResponse> result = emailBuilder
        .to("user@example.com")
        .cc("copy@example.com")
        .subject("Report")
        .text("Your report is attached.")
        .html("<p>Your report is attached.</p><img src='cid:logo'>")
        .attachment("report.txt", "Report contents".getBytes(StandardCharsets.UTF_8), "text/plain")
        .inline("logo", "logo.png", logoBytes, "image/png") // supply your image bytes
        .retry(3)
        .onFailure(context -> {
            // Inspect context.response() and record final delivery failure.
        })
        .async()
        .send();
```

Configure retry and callbacks before `.async()`, which returns an `AsyncEmailDispatch` exposing `send()`. Handle both failed responses and exceptional completion of the future. `onFailure` runs after delivery retries are exhausted; it is not a callback for invalid input or executor rejection.

The stream attachment overloads read and close the stream while composing the message. An `InputStreamSource` should return a fresh stream for each read so that SMTP retries can reread the attachment.

## Configuration reference

All properties below are under `vg.lite-email`:

| Property | Default | Meaning |
| --- | --- | --- |
| `enabled` | `false` | Enables real delivery |
| `username`, `password` | unset | Credentials required by the default SMTP implementation |
| `from` | `username` | Default sender address |
| `from-name`, `reply-to` | unset | Default display name and reply address |
| `sender` | unset | Alias supplying username/from when absent |
| `host`, `port` | inferred | SMTP endpoint; set both for an explicit endpoint |
| `provider-auto-detect` | `true` | Infers endpoint when host/port are not both set |
| `protocol` | inferred | `SMTP` or `SMTPS` |
| `ssl`, `starttls` | inferred | SSL and STARTTLS toggles |
| `auth` | `true` | SMTP authentication toggle; default implementation still requires credentials |
| `connection-timeout`, `timeout`, `write-timeout` | `5000` | Socket timeouts in milliseconds |
| `async.enabled` | `true` | Uses a pool; false executes on the calling thread |
| `async.virtual-threads` | `true` | Virtual threads; false selects a fixed platform pool |
| `async.concurrency-limit` | `256` | Maximum concurrent virtual tasks / platform pool size |
| `async.thread-name-prefix` | `vg-lite-email-` | Thread name prefix |
| `retry.enabled` | `true` | Enables retry policy |
| `retry.default-attempts` | `1` | Total attempts when the message has no override |
| `retry.max-attempts` | `3` | Global cap on total attempts |
| `retry.initial-delay` | `1s` | Delay before the first retry |
| `retry.multiplier` | `2.0` | Exponential backoff multiplier |
| `retry.max-delay` | `30s` | Upper bound on retry delay |
| `logging.enabled` | `true` | Starter logging |
| `logging.level` | `INFO` | `DEBUG`, `INFO`, `WARN`, `ERROR` |

`.retry(3)` means at most three total attempts, including the first send, subject to the global cap. `.retry()` requests two total attempts. The default SMTP channel retries codes `421`, `450`, `451`, and `452`; permanent or unknown failures are not retried by default. Both the channel and retry policy must allow another attempt.

The bounded virtual executor waits for a permit when the concurrency limit is reached, so submitting an async send may block under load. The platform pool uses a queue. A custom `liteEmailExecutor` bean can supply different admission or queue behavior. Setting `async.enabled: false` runs async requests on the caller and returns a completed future.

When `enabled` is false or absent, the default no-op sender returns `success=false`, `errorCode=-2`, `channel=NOOP`. Credentials, retry infrastructure, and the executor are not required. The switch selects default implementations at application startup; it does not dynamically reconfigure an already running context. User-supplied beans retain their own behavior.

## Extension points

Register implementations as Spring beans; auto-configuration backs off for the corresponding contract.

| Type under `io.github.virtualgemini.vgliteemail` | Purpose |
| --- | --- |
| `api.sender.EmailSender` | Replace the entire send pipeline |
| `spi.channel.EmailChannel` | Replace SMTP and retain the default pipeline |
| `spi.policy.RetryPolicy` | Retry eligibility and delay |
| `spi.policy.EmailExceptionTranslator` | Translate exceptions caught by the sender |
| `spi.hook.EmailSendInterceptor` | Transform requests before and responses after delivery |
| `spi.hook.EmailSendListener` | Observe final success/failure |
| `api.builder.EmailBuilderFactory` | Create custom builders |
| Bean named `liteEmailExecutor` | Customize async execution |

Abstract bases are provided in `spi.builder.AbstractEmailBuilder`, `spi.sender.AbstractEmailSender`, and `spi.channel.AbstractEmailChannel`. The SMTP channel translates its own mail failures before returning a response; replacing the sender's translator does not replace that SMTP translation.

## Upgrading from 0.1.64

| Previous usage | New usage |
| --- | --- |
| Java 8+, Spring Boot 2 | Java 21+, Spring Boot 3 |
| `core.EmailBuilder` | `api.builder.EmailBuilder` |
| `core.SendRequest` / `core.SendResponse` | `api.message.SendRequest` / `api.message.SendResponse` |
| Implicitly enabled sending | Set `vg.lite-email.enabled: true` |
| `.async().retry(3).send()` | `.retry(3).async().send()` |
| Separate `spring-boot-starter-mail` dependency | Included transitively |
| Mutable composition | Retain each returned immutable builder |

Maven group/artifact coordinates and the `vg.lite-email` prefix are retained. API package layout, runtime requirements, execution settings, and retry semantics must be reviewed when upgrading. The old READMEs and implementation remain accessible on `master`.

## Development

```bash
mvn verify
```

Tests cover enabled/disabled auto-configuration, custom contracts, immutable composition, SMTP MIME content, attachments, retries, callbacks, and executor lifecycle without contacting a live SMTP server. GitHub Actions runs verification on Java 21 and 25 for `main` pushes and pull requests.

## License and contributions

The repository retains its [Apache 2.0 license](./LICENSE). Imported Velox email code retains its [MIT notice](./LICENSE-MIT), also included in the JAR. [Migration history](./docs/HISTORY.md) records provenance.

Issues and pull requests are welcome. Include a reproducible example and relevant test coverage for behavior changes.
