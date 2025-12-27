#### Available in:
- [English](docs/en/README.md)
- [简体中文](docs/zh-CN/README.md)
- [繁體中文](docs/zh-TW/README.md)

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
