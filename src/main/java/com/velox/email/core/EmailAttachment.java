package com.velox.email.core;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class EmailAttachment {

    private final String filename;
    private final String contentType;
    private final InputStreamSource source;
    private final boolean inline;
    private final String contentId;

    private EmailAttachment(String filename,
                            String contentType,
                            InputStreamSource source,
                            boolean inline,
                            String contentId) {
        this.filename = Objects.requireNonNull(filename, "filename must not be null");
        this.contentType = contentType;
        this.source = Objects.requireNonNull(source, "source must not be null");
        this.inline = inline;
        this.contentId = contentId;
    }

    public static EmailAttachment attachment(Resource resource) {
        String filename = resource.getFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Attachment resource filename must not be blank");
        }
        return new EmailAttachment(filename, null, resource, false, null);
    }

    public static EmailAttachment attachment(File file) {
        return new EmailAttachment(file.getName(), null, new FileSystemResource(file), false, null);
    }

    public static EmailAttachment attachment(String filename, byte[] content, String contentType) {
        return new EmailAttachment(filename, contentType, new ByteArrayResource(content), false, null);
    }

    public static EmailAttachment attachment(String filename, InputStream inputStream, String contentType) {
        return attachment(filename, readBytes(inputStream), contentType);
    }

    public static EmailAttachment attachment(String filename, InputStreamSource source, String contentType) {
        return new EmailAttachment(filename, contentType, source, false, null);
    }

    public static EmailAttachment inline(String contentId, String filename, byte[] content, String contentType) {
        return new EmailAttachment(filename, contentType, new ByteArrayResource(content), true, contentId);
    }

    public static EmailAttachment inline(String contentId, String filename, InputStream inputStream, String contentType) {
        return inline(contentId, filename, readBytes(inputStream), contentType);
    }

    public static EmailAttachment inline(String contentId, String filename, InputStreamSource source, String contentType) {
        return new EmailAttachment(filename, contentType, source, true, contentId);
    }

    private static byte[] readBytes(InputStream inputStream) {
        try (InputStream in = inputStream) {
            return in.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Failed to read email attachment input stream", exception);
        }
    }

    public String filename() {
        return filename;
    }

    public String contentType() {
        return contentType;
    }

    public InputStreamSource source() {
        return source;
    }

    public boolean inline() {
        return inline;
    }

    public String contentId() {
        return contentId;
    }
}
