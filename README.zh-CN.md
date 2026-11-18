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
builder.to("email-username@example.com" ).subject("Test").text("Hello, World!").retry(3).async().send();
```
```java
builder                                      // 注入的 EmailBuilder
        .to("email-username@example.com" )   // set recipient
        .subject("Test")                     // set subject
        .text("Hello, World!")               // set content
        .retry(3)                            // up to 3 total attempts
        .async()                             // send asynchronously
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
Java: 需要 Java 21 及以上，已在 Java 21 和 25 上验证。

Spring Boot: 使用 Spring Boot 3 和 Jakarta Mail，基于 Spring Boot 3.4.5 构建和测试。

Spring Boot 2 / 4: 新版不支持 Spring Boot 2，尚未验证 Spring Boot 4。

协议: 当前仅支持 SMTP。更多协议将在未来版本中添加。

### 构建 JAR
当前开发版本为 `1.0.0-SNAPSHOT`，请从源码构建 JAR：

```bash
git clone --branch main https://github.com/VirtualGemini/vg-lite-email-spring-boot-starter.git
cd vg-lite-email-spring-boot-starter
mvn clean package
```

### Maven 安裝
在项目目录执行以下命令，构建 starter 并安装到本地 Maven 仓库。

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
该命令会同时安装 JAR、项目 POM 及其依赖声明。

### Maven 依賴
在你的 `pom.xml` 文件中添加以下依赖：
```xml
<!-- VG Lite Email -->
<dependency>
    <groupId>io.github.virtualgemini</groupId>
    <artifactId>vg-lite-email-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
新版已传递引入 `spring-boot-starter-mail`，无需再单独声明。别忘了重新加载项目。

### Spring Boot 配置
在你的 `application.yml` 文件中添加以下配置：
```yaml
vg:
  lite-email:
    enabled: true                       # 启用邮件发送
    host: smtp.example.com              # SMTP 服务器
    port: 587                           # STARTTLS 端口
    username: ${SMTP_USERNAME}           # SMTP 用户名
    password: ${SMTP_PASSWORD}           # SMTP 密码或授权码
    from: ${SMTP_USERNAME}               # 发件地址
    ssl: false
    starttls: true
    provider-auto-detect: false
```
设置环境变量 `SMTP_USERNAME`、`SMTP_PASSWORD`，并按邮箱服务商要求替换 SMTP 地址及端口。`enabled` 默认为 `false`，未设置 `from` 时使用 `username`。

请注意 `application.yml` 文件的格式和缩进；不正确的空白可能会防止电子邮件设置的正确加载。

### 一切就绪，我们可以起飞了！
#### 随意集成到你的项目中并看到效果！

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
builder 是不可变的，请使用链式调用或保存返回值。`.retry(3)` 表示最多尝试三次，包含首次发送，且必须放在 `.async()` 前。异步 `send()` 返回 `CompletableFuture<SendResponse>`，需要检查响应及异常完成情况；同步发送可用 `.sendSync()`，并检查 `response.success()`。

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

- **第三步**: 检查响应。上面的 Controller 会在 HTTP 响应中返回异步发送结果，`success: true` 表示 SMTP 服务器已接受邮件。成功响应示例如下：
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

- **第四步**: 现在！你可以检查你的邮件。

## 贡献
仓库保留 [Apache 2.0 许可证](./LICENSE)，提取的邮件模块保留 [MIT 声明](./LICENSE-MIT)。

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
