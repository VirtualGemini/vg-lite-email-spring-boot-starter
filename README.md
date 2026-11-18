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

Fluent API – Chainable for clean and readable email creation.EmailBuilder

Customizable Executors – Configure thread pools for async operations.


## Quick Start
### Version & Compatibility

- **Java**: Requires Java 21 or higher. Tested with Java 21 and 25.
- **Spring Boot**: Uses Spring Boot 3 and Jakarta Mail; built and tested against Spring Boot 3.4.5.
- **Spring Boot 2 / 4**: The new version does not support Spring Boot 2; Spring Boot 4 has not been verified.
- **Protocol**: Currently supports **SMTP**. More protocols will be added in future releases.

### Download Jar
The previous `0.1.64` JAR remains available at the following link:

- https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/releases/tag/v0.1.64

The current `main` development version is `1.0.0-SNAPSHOT`. Build its JAR from source:

```bash
git clone --branch main https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter.git
cd vg-lite-email-spring-boot-starter
mvn clean package
```

The old implementation remains on `master`; see [migration history](./docs/HISTORY.md) for the preserved source commits.

### Maven Install
Run the following command from the project directory to install the JAR and its dependency POM locally. You can also use `mvn clean install` directly.

#### linux shell:
```bash
mvn install:install-file \
  -Dfile=target/vg-lite-email-spring-boot-starter-1.0.0-SNAPSHOT.jar \
  -DgroupId=io.github.virtualgemini \
  -DartifactId=vg-lite-email-spring-boot-starter \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DpomFile=pom.xml
```
#### windows cmd:
```bash
mvn install:install-file -Dfile=target/vg-lite-email-spring-boot-starter-1.0.0-SNAPSHOT.jar -DgroupId=io.github.virtualgemini -DartifactId=vg-lite-email-spring-boot-starter -Dversion=1.0.0-SNAPSHOT -Dpackaging=jar -DpomFile=pom.xml
```
#### windows powershell:
```bash
mvn install:install-file `
  -Dfile=target/vg-lite-email-spring-boot-starter-1.0.0-SNAPSHOT.jar `
  -DgroupId=io.github.virtualgemini `
  -DartifactId=vg-lite-email-spring-boot-starter `
  -Dversion=1.0.0-SNAPSHOT `
  -Dpackaging=jar `
  -DpomFile=pom.xml
```
In the above command, `-Dfile` is the path to the built JAR and `-DpomFile` is the project POM, which preserves its dependency declarations.

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
    enabled: true                        # enable email sending
    sender: your-email@example.com       # your email address
    password: your_authorization_code    # your email SMTP password
```
`enabled` defaults to `false`. The existing `sender` property still supplies the username and default sender address; common SMTP providers are inferred from its domain. For other servers, configure both `host` and `port` explicitly.

Please pay attention to the format and indentation in your `application.yml`; incorrect spacing may prevent proper loading of email settings.

### Everything's Ready, We Can Take Off!
#### Feel free to integrate it into your project and see it in action!

```java
import io.github.virtualgemini.vgliteemail.api.builder.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    
    @Autowired
    EmailBuilder builder;

    @PostMapping("/send")
    public void sendEmail(){
        builder
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

- **Step 3**: Check the HTTP response and console log. Status code 200 means the request was handled; check the async result separately to confirm SMTP acceptance.
```http request
POST http://localhost:8080/send

HTTP/1.1 200 
Content-Length: 0
Date: Sat, 22 Nov 2025 12:29:35 GMT
Keep-Alive: timeout=60
Connection: keep-alive

<Response body is empty>

Response code: 200; Time: 87ms (87 ms); Content length: 0 bytes (0 B)
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
