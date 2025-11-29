# VG Lite Email Spring Boot Starter

VG Lite Email is a lightweight, robust, and developer-friendly email solution built on Spring Boot. It provides an easy-to-use API for sending both synchronous and asynchronous emails with retry mechanisms, while integrating seamlessly into Spring Boot applications.

## Demo
Just one line to send an email:
```java
builder.to("email-username@example.com" ).subject("Test").text("Hello, World!").async().retry(3).send();
```
```java
builder                                      // new EmailBuilder()
        .to("email-username@example.com" )   // set recipient
        .subject("Test")                     // set subject
        .text("Hello, World!")               // set content
        .async()                             // send asynchronously
        .retry(3)                            // retry 3 if failed
        .send();                             // send email
```

## Why it's crazy good

- **One line is enough**: Forget about manually wiring JavaMailSender, session, and exception handling.

- **Built-in async**: Asynchronous switch, one-click activation, freeing your main thread. (Of course, you can also choose synchronous sending if you prefer.)

- **Retry & backoff mechanism**: Automatically retries failed sends with exponential backoff. You can also customize the retry rules and the number of attempts to fit your needs.

- **Fluent Builder API**: Your code reads like poetry.

- **Production-ready**: Handles common pitfalls, edge cases, and failures gracefully.

- **Plug & Play**: Add the jar, configure your sender, and you’re done — no headaches.

## Features

Easy Integration – Simple auto-configuration with Spring Boot.

Asynchronous Sending – Send emails asynchronously with a single method call.

Retry Mechanism – Built-in configurable retry for failed emails.

Fluent API – Chainable for clean and readable email creation.EmailBuilder

Customizable Executors – Configure thread pools for async operations.


## Quick Start
### Version & Compatibility

- **Java**: Supports Java 8, 11, and 17.
- **Spring Boot**: Requires Spring Boot 2.6 or higher. Very old versions may cause issues.
- **Spring 3+**: Not supported yet. This is not your fault—we are working on it and will update soon.
- **Protocol**: Currently supports **SMTP**. More protocols will be added in future releases.

Enjoy the **beta release** for now and start sending emails effortlessly!
### Download Jar
You can download the jar file from the following link:
- https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/releases/tag/v0.1.44
### Maven Install
#### linux shell:
```bash
mvn install:install-file \
  -Dfile=vg-lite-email-spring-boot-starter-0.1.44.jar \
  -DgroupId=io.github.virtualgemini \
  -DartifactId=vg-lite-email-spring-boot-starter \
  -Dversion=0.1.44 \
  -Dpackaging=jar
```
#### windows cmd:
```bash
mvn install:install-file -Dfile=vg-lite-email-spring-boot-starter-0.1.44.jar -DgroupId=io.github.virtualgemini -DartifactId=vg-lite-email-spring-boot-starter -Dversion=0.1.44 -Dpackaging=jar
```
#### windows powershell:
```bash
mvn install:install-file `
  -Dfile=vg-lite-email-spring-boot-starter-0.1.44.jar `
  -DgroupId=io.github.virtualgemini `
  -DartifactId=vg-lite-email-spring-boot-starter `
  -Dversion=0.1.44 `
  -Dpackaging=jar
```
It should be noted that in the above command, the `-Dfile` parameter needs to be replaced with the path to the jar file you downloaded.

### Maven Dependency
Add the following dependency to your `pom.xml`:
```xml
<!-- VG Lite Email -->
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>0.1.44</version>
</dependency>
<!-- Spring Boot Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
Please don't forget to reload.
### Spring Boot Configuration
Add the following dependency to your `application.yml`:
```yaml
vg:
  lite-email:
    sender: your-email@example.com       # your email address
    password: your_authorization_code    # your email SMTP password
```
Please pay attention to the format and indentation in your `application.yml`; incorrect spacing may prevent proper loading of email settings.

### Everything's Ready, We Can Take Off!
#### Feel free to integrate it into your project and see it in action!

```java
import io.github.virtualgemini.vgliteemail.core.EmailBuilder;
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
                .async()
                .retry()
                .send();
    }
}
```
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
- **Step 3**: You can see the console log, status code 200 is OK.
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

## Looking ahead
we’re planning to add scheduled tasks, batch sending, email templates, message queue integration, and more…

## Conclusion & Thanks
Core-ready, almost magic: While some features are still polishing and a few may have minor rough edges, the core email sending functionality is solid and fully usable — one line to send, async & retry ready, your emails just fly!

Thank you for checking out **VG Lite Email Spring Boot Starter**! We hope it makes sending emails in your projects effortless and enjoyable.

If you have ideas for improvements, feature requests, or find any bugs, don’t hesitate to open an issue or submit a pull request. Every bit of feedback helps us make this tool even better.

If you like the project, please give it a ⭐ on GitHub — your support keeps us motivated!

Happy coding and smooth emailing! 
