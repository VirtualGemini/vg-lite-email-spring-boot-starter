package com.velox.email.api.builder;

import com.velox.email.api.message.EmailFailureContext;
import com.velox.email.api.message.SendRequest;
import com.velox.email.api.message.SendResponse;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Consumer;

public interface EmailMessageBuilder<T extends EmailMessageBuilder<T>> {

    T to(String... to);

    T to(Collection<String> to);

    T cc(String... cc);

    T cc(Collection<String> cc);

    T bcc(String... bcc);

    T bcc(Collection<String> bcc);

    T replyTo(String replyTo);

    T from(String from);

    T fromName(String fromName);

    T subject(String subject);

    T text(String text);

    T html(String html);

    T html(boolean html);

    T attachment(Resource resource);

    T attachment(File file);

    T attachment(String filename, byte[] content);

    T attachment(String filename, byte[] content, String contentType);

    T attachment(String filename, InputStream inputStream);

    T attachment(String filename, InputStream inputStream, String contentType);

    T attachment(String filename, InputStreamSource source, String contentType);

    T inline(String contentId, String filename, byte[] content, String contentType);

    T inline(String contentId, String filename, InputStream inputStream, String contentType);

    T inline(String contentId, String filename, InputStreamSource source, String contentType);

    T retry(int maxAttempts);

    T retry();

    T async();

    T onFailure(Consumer<EmailFailureContext> failureHook);

    SendRequest build();

    SendResponse sendSync();

    Object send();
}
