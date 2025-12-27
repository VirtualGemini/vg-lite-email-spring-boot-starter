<h2 align="center" id="top">VG Lite Email Spring Boot Starter</h2>
<p>一个轻量级、强大且开发者友好的邮件解决方案，基于 Spring Boot 构建。它提供了一个易于使用的链式 API，用于发送同步和异步邮件，并支持重试机制，可以无缝集成到 Spring Boot 应用程序中，让你的代码如同诗歌。</p>
<div align="center"><a href="./README.md">English</a> | 简体中文</div>

<br />
<div align="center">

[![license](https://img.shields.io/badge/license-Apache%202.0-green.svg)](./LICENSE) [![github stars](https://img.shields.io/github/stars/VirtualGemini/vg-lite-email-spring-boot-starter)](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/stargazers) [![github forks](https://img.shields.io/github/forks/VirtualGemini/vg-lite-email-spring-boot-starter)](https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter/network/members)

</div>

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

## 贡献
我们真诚欢迎并感谢每一位贡献者的支持！无论您有新想法、功能建议还是代码优化，都可以通过以下方式参与：

提交 Pull Request：分享您的代码，助力项目成长。

创建 GitHub Issue：提出 bug 反馈或新功能建议，让我们一起完善。

您的每一点贡献都让这个项目更进一步！快来加入我们的开源社区吧！

## 展望未来

我们计划添加定时任务、批量发送、邮件模板、消息队列集成等功能......

## 结论与感谢

核心功能就绪，几乎完美：虽然一些功能仍在打磨中，可能还有一些小问题，但核心的邮件发送功能非常稳定且完全可用 —— 一行代码发送邮件，支持异步和重试，你的邮件飞起来了！

感谢你查看 VG Lite Email Spring Boot Starter！我们希望它能让你在项目中发送邮件变得轻松愉快。

如果你有改进建议、功能请求或发现任何 bug，请随时提出问题或提交拉取请求。每一条反馈都帮助我们让这个工具变得更好。

如果你喜欢这个项目，请在 GitHub 上给它一个 ⭐ —— 你的支持是我们的动力！

祝编程愉快，邮件发送顺利！

<br>
<div align="center"><a href="#top">回到顶部</a></div>
<br>
