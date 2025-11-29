/*
 * Copyright 2025 Your VirtualGemini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.github.virtualgemini.vgliteemail.exception;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/28 20:54
 */
public final class EmailExceptions {

    private EmailExceptions() {}

    public static EmailConfigException missing(String field) {
        return new EmailConfigException("Required email config missing: " + field);
    }

    public static EmailSendException sendError(Exception e) {
        return new EmailSendException("Failed to send email", e);
    }

    public static EmailRetryException retryFail(int times) {
        return new EmailRetryException("Retry failed after " + times + " attempts");
    }
}

