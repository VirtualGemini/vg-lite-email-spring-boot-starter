<h2 align="center" id="top">VG Lite Email Spring Boot Starter</h2>
<p>A lightweight, robust, and developer-friendly email solution built on Spring Boot. It provides an easy-to-use API for sending both synchronous and asynchronous emails with retry mechanisms, while integrating seamlessly into Spring Boot applications, your code reads like poetry.</p>
<div align="center">English | <a href="./README.zh-CN.md">简体中文</a></div>

<br />
<div align="center">

[![license](https://img.shields.io/badge/license-Apache%202.0-green.svg)](./LICENSE) [![github stars](https://img.shields.io/github/stars/VirtualGemini/vg-lite-email-spring-boot-starter)](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/stargazers) [![github forks](https://img.shields.io/github/forks/VirtualGemini/vg-lite-email-spring-boot-starter)](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/network/members)

</div>

## Demo
Just one line to send an email:
```java
builder.to("email-username@example.com" ).subject("Test").text("Hello, World!").retry(3).async().send();
```
```java
builder                                      // injected EmailBuilder
        .to("email-username@example.com" )   // set recipient
        .subject("Test")                     // set subject
        .text("Hello, World!")               // set content
        .retry(3)                            // up to 3 total attempts
        .async()                             // send asynchronously
        .send();                             // send email
```

## Features

Easy Integration – Simple auto-configuration with Spring Boot.

Asynchronous Sending – Send emails asynchronously with a single method call.

Retry Mechanism – Built-in configurable retry for failed emails.

Fluent API – Chainable for clean and readable email creation.

Customizable Executors – Configure thread pools for async operations.


## Quick Start
### Version & Compatibility

- **Java**: Requires Java 21 or higher. Tested with Java 21 and 25.
- **Spring Boot**: Uses Spring Boot 3 and Jakarta Mail; built and tested against Spring Boot 3.4.5.
- **Spring Boot 2 / 4**: The new version does not support Spring Boot 2; Spring Boot 4 has not been verified.
- **Protocol**: Currently supports **SMTP**. More protocols will be added in future releases.

### Build Jar
The current development version is `1.0.0-SNAPSHOT`. Build its JAR from source:

```bash
git clone --branch main https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter.git
cd vg-lite-email-spring-boot-starter
mvn clean package
```

### Maven Install
Run the following command from the project directory to build and install the starter into your local Maven repository.

#### linux shell:
```bash
mvn clean install
```
#### windows cmd:
```bash
mvn clean install
```
#### windows powershell:
```bash
mvn clean install
```
This installs the JAR together with its POM and dependency declarations.

### Maven Dependency
Add the following dependency to your `pom.xml`:
```xml
<!-- VG Lite Email -->
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
`spring-boot-starter-mail` is now included transitively, so it does not need to be declared separately. Please don't forget to reload.
### Spring Boot Configuration
Add the following configuration to your `application.yml`:
```yaml
vg:
  lite-email:
    enabled: true                       # enable email sending
    host: smtp.example.com              # SMTP server
    port: 587                           # STARTTLS port
    username: ${SMTP_USERNAME}           # SMTP username
    password: ${SMTP_PASSWORD}           # SMTP password or authorization code
    from: ${SMTP_USERNAME}               # sender address
    ssl: false
    starttls: true
    provider-auto-detect: false
```
Set the environment variables `SMTP_USERNAME` and `SMTP_PASSWORD`, and replace the SMTP endpoint with your provider's settings. `enabled` defaults to `false`; `from` falls back to `username` when omitted.

Please pay attention to the format and indentation in your `application.yml`; incorrect spacing may prevent proper loading of email settings.

### Everything's Ready, We Can Take Off!
#### Feel free to integrate it into your project and see it in action!

```java
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import io.github.virtualgemini.vgliteemail.api.message.SendResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {
    
    @Autowired
    EmailBuilder builder;

    @PostMapping("/send")
    public CompletableFuture<SendResponse> sendEmail(){
        return builder
                .to("email-username@example.com" )
                .subject("Test")
                .text("Hello, World!")
                .retry(3)
                .async()
                .send();
    }
}
```
The builder is immutable: chain calls or retain their return values. `.retry(3)` allows up to three total attempts, including the first send, and must precede `.async()`. Async `send()` returns a `CompletableFuture<SendResponse>`; check its response or exceptional completion to determine the result. For synchronous sending, use `.sendSync()` and check `response.success()`.

#### Don’t know how to get started? Don’t worry, I’ve got you covered!

- **Step 1**: Start your project, if you are using Spring Boot, you can start it here with one click.
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SendMailDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendMailDemoApplication.class, args);
    }

}
```

- **Step 2**: Add HTTP REQUEST file, you can run it directly.
```http request
POST http://localhost:8080/send
Content-Type: application/json

{}

###
```

- **Step 3**: Check the response. With the controller above, the HTTP response includes the async send result; `success: true` means the SMTP server accepted the message. A successful response looks like:
```http request
POST http://localhost:8080/send

HTTP/1.1 200
Content-Type: application/json

{
  "success": true,
  "msgId": "<message-id@example.com>",
  "error": null,
  "errorCode": 0,
  "attempts": 1,
  "channel": "SMTP"
}
```

- **Step 4**: Now! you can check your email.

## Contributing
The repository retains its [Apache 2.0 license](./LICENSE); the imported email module retains its [MIT notice](./LICENSE-MIT).

We sincerely welcome and appreciate the support of every contributor! Whether you have new ideas, feature suggestions, or code optimizations, you can participate in the following ways:

Submit Pull Requests: Share your code and help the project grow.

Create GitHub Issues: Provide bug feedback or new feature suggestions to help us improve together.

Every contribution you make takes this project one step further! Come join our open source community!

## Looking ahead
we’re planning to add scheduled tasks, batch sending, email templates, message queue integration, and more...

## Conclusion & Thanks
Core-ready, almost magic: While some features are still polishing and a few may have minor rough edges, the core email sending functionality is solid and fully usable — one line to send, async & retry ready, your emails just fly!

Thank you for checking out **VG Lite Email Spring Boot Starter**! We hope it makes sending emails in your projects effortless and enjoyable.

If you have ideas for improvements, feature requests, or find any bugs, don’t hesitate to open an issue or submit a pull request. Every bit of feedback helps us make this tool even better.

If you like the project, please give it a ⭐ on GitHub — your support keeps us motivated!

Happy coding and smooth emailing! 

<br>
<div align="center"><a href="#top">Back to Top</a></div>
<br>
