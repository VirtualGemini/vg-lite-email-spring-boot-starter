### languages:

#### English
- [English](#English)
- [简体中文](#简体中文)
- [繁體中文](#繁體中文)
- 
# VG Lite Email Spring Boot Starter

VG Lite Email is a lightweight, robust, and developer-friendly email solution built on Spring Boot. It provides an easy-to-use API for sending both synchronous and asynchronous emails with retry mechanisms, while integrating seamlessly into Spring Boot applications, your code reads like poetry.

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

### Download Jar
You can download the jar file from the following link:
- https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/releases/tag/v0.1.64
### Maven Install
#### linux shell:
```bash
mvn install:install-file \
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar \
  -DgroupId=io.github.virtualgemini \
  -DartifactId=vg-lite-email-spring-boot-starter \
  -Dversion=0.1.64 \
  -Dpackaging=jar
```
#### windows cmd:
```bash
mvn install:install-file -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar -DgroupId=io.github.virtualgemini -DartifactId=vg-lite-email-spring-boot-starter -Dversion=0.1.64 -Dpackaging=jar
```
#### windows powershell:
```bash
mvn install:install-file `
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar `
  -DgroupId=io.github.virtualgemini `
  -DartifactId=vg-lite-email-spring-boot-starter `
  -Dversion=0.1.64 `
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
    <version>0.1.64</version>
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
                .retry(3)
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

---

#### 简体中文
- [English](#English)
- [简体中文](#简体中文)
- [繁體中文](#繁體中文)

# VG Lite Email Spring Boot Starter

VG Lite Email 是一个轻量级、强大且开发者友好的邮件解决方案，基于 Spring Boot 构建。它提供了一个易于使用的 API，用于发送同步和异步邮件，并支持重试机制，可以无缝集成到 Spring Boot 应用程序中，让你的代码如同诗歌。

## 演示
只需要一行代码来发送邮件:
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

## 特性

简单集成 – 与 Spring Boot 无缝自动配置。

异步发送 – 通过一个方法调用异步发送邮件。

重试机制 – 为失败的邮件内置可配置的重试机制。

流畅的 API – 支持链式调用，生成简洁可读的邮件。

可定制的执行器 – 配置异步操作的线程池。


## 快速开始
### 版本与兼容性
Java: 支持 Java 8、11 和 17。

Spring Boot: 需要 Spring Boot 2.6 或更高版本。非常老的版本可能会有问题。

Spring 3+: 暂不支持。我们正在努力开发，并将很快更新。

协议: 当前仅支持 SMTP。更多协议将在未来版本中添加。

### 下载 JAR
你可以通过以下链接下载 JAR 文件:
- https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/releases/tag/v0.1.64
### Maven 安裝
#### linux shell:
```bash
mvn install:install-file \
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar \
  -DgroupId=io.github.virtualgemini \
  -DartifactId=vg-lite-email-spring-boot-starter \
  -Dversion=0.1.64 \
  -Dpackaging=jar
```
#### windows cmd:
```bash
mvn install:install-file -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar -DgroupId=io.github.virtualgemini -DartifactId=vg-lite-email-spring-boot-starter -Dversion=0.1.64 -Dpackaging=jar
```
#### windows powershell:
```bash
mvn install:install-file `
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar `
  -DgroupId=io.github.virtualgemini `
  -DartifactId=vg-lite-email-spring-boot-starter `
  -Dversion=0.1.64 `
  -Dpackaging=jar
```
注意，在上述命令中，-Dfile 参数需要替换为你下载的 JAR 文件的路径。

### Maven 依賴
在你的 `pom.xml` 文件中添加以下依赖：
```xml
<!-- VG Lite Email -->
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>0.1.64</version>
</dependency>
<!-- Spring Boot Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
别忘了重新加载项目。

### Spring Boot 配置
在你的 `application.yml` 文件中添加以下配置：
```yaml
vg:
  lite-email:
    sender: your-email@example.com       # your email address
    password: your_authorization_code    # your email SMTP password
```
请注意 `application.yml` 文件的格式和缩进；不正确的空白可能会防止电子邮件设置的正确加载。

### 一切就绪，我们可以起飞了！
#### 随意集成到你的项目中并看到效果！

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
                .retry(3)
                .send();
    }
}
```
#### 不知道如何开始？别担心，我已经准备好了！
- **第一步**: 启动你的项目，如果你使用 Spring Boot，可以一键启动。
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
- **第二步**: 添加 HTTP 请求文件，可以直接运行。
```http request
POST http://localhost:8080/send
Content-Type: application/json

{}

###
```
- **第三步**: 你可以在控制台看到日志，状态码 200 表示成功。
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
- **第四步**: 现在！你可以检查你的邮件。

## 展望未来

我们计划添加定时任务、批量发送、邮件模板、消息队列集成等功能……

## 结论与感谢

核心功能就绪，几乎完美：虽然一些功能仍在打磨中，可能还有一些小问题，但核心的邮件发送功能非常稳定且完全可用 —— 一行代码发送邮件，支持异步和重试，你的邮件飞起来了！

感谢你查看 VG Lite Email Spring Boot Starter！我们希望它能让你在项目中发送邮件变得轻松愉快。

如果你有改进建议、功能请求或发现任何 bug，请随时提出问题或提交拉取请求。每一条反馈都帮助我们让这个工具变得更好。

如果你喜欢这个项目，请在 GitHub 上给它一个 ⭐ —— 你的支持是我们的动力！

祝编程愉快，邮件发送顺利！

---

#### 繁體中文

- [English](#English)
- [简体中文](#简体中文)
- [繁體中文](#繁體中文)

# VG Lite Email Spring Boot Starter

VG Lite Email 是一個輕量級、強大且開發者友好的郵件解決方案，基於 Spring Boot 構建。它提供了一個易於使用的 API，用於發送同步和異步郵件，並支持重試機制，可以無縫集成到 Spring Boot 應用程序中，讓你的代碼如同詩歌。

## 演示
只需要一行代碼來發送郵件：
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

## 特性

簡單集成 – 與 Spring Boot 無縫自動配置。

異步發送 – 通過一個方法調用異步發送郵件。

重試機制 – 為失敗的郵件內置可配置的重試機制。

流暢的 API – 支持鏈式調用，生成簡潔可讀的郵件。

可定制的執行器 – 配置異步操作的線程池。


## 快速開始
### 版本與兼容性
Java: 支持 Java 8、11 和 17。

Spring Boot: 需要 Spring Boot 2.6 或更高版本。非常舊的版本可能會有問題。

Spring 3+: 暫不支持。我們正在努力開發，並將很快更新。

協議: 當前仅支持 SMTP。更多協議將在未來版本中添加。

### 下載 JAR
你可以通過以下鏈接下載 JAR 文件：
- https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/releases/tag/v0.1.64
### Maven 安裝
#### linux shell:
```bash
mvn install:install-file \
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar \
  -DgroupId=io.github.virtualgemini \
  -DartifactId=vg-lite-email-spring-boot-starter \
  -Dversion=0.1.64 \
  -Dpackaging=jar
```
#### windows cmd:
```bash
mvn install:install-file -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar -DgroupId=io.github.virtualgemini -DartifactId=vg-lite-email-spring-boot-starter -Dversion=0.1.64 -Dpackaging=jar
```
#### windows powershell:
```bash
mvn install:install-file `
  -Dfile=vg-lite-email-spring-boot-starter-0.1.64.jar `
  -DgroupId=io.github.virtualgemini `
  -DartifactId=vg-lite-email-spring-boot-starter `
  -Dversion=0.1.64 `
  -Dpackaging=jar
```
注意，在上述命令中，-Dfile 參數需要替換為你下載的 JAR 文件的路徑。

### Maven 依賴
在你的 `pom.xml` 文件中添加以下依賴：
```xml
<!-- VG Lite Email -->
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>0.1.64</version>
</dependency>
<!-- Spring Boot Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
別忘了重新加載項目。

### Spring Boot 配置
在你的 `application.yml` 文件中添加以下配置：
```yaml
vg:
  lite-email:
    sender: your-email@example.com       # your email address
    password: your_authorization_code    # your email SMTP password
```
請注意 `application.yml` 檔案的格式和縮進；不正确的空白可能會防止電子郵件設定的正確加載。

### 一切就绪，我們可以起飛了！
#### 隨意集成到你的項目中並看到效果！

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
                .retry(3)
                .send();
    }
}
```
#### 不知道如何開始？別擔心，我已經準備好了！
- **第一步**: 啟動你的項目，如果你使用 Spring Boot，可以一鍵啟動。
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
- **第二步**: 添加 HTTP 請求文件，可以直接運行。
```http request
POST http://localhost:8080/send
Content-Type: application/json

{}

###
```
- **第三步**: 你可以在控制台看到日誌，狀態碼 200 表示成功。
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
- **第四步**: 現在！你可以檢查你的郵件。

## 展望未來

我們計劃添加定時任務、批量發送、郵件模板、消息隊列集成等功能……

## 結論與感謝

核心功能就緒，幾乎完美：雖然一些功能仍在打磨中，可能還有一些小問題，但核心的郵件發送功能非常穩定且完全可用 —— 一行代碼發送郵件，支持異步和重試，你的郵件飛起來了！

感謝你查看 VG Lite Email Spring Boot Starter！我們希望它能讓你在項目中發送郵件變得輕鬆愉快。

如果你有改進建議、功能請求或發現任何 bug，請隨時提出問題或提交拉取請求。每一條反饋都幫助我們讓這個工具變得更好。

如果你喜歡這個項目，請在 GitHub 上給它一個 ⭐ —— 你的支持是我們前進的動力！

祝編程愉快，郵件發送順利！
