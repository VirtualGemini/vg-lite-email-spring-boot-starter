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
package io.github.virtualgemini.vgliteemail.api;

import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Top-level interface for e-mail sending channels.
 * <p>All implementations must be thread-safe and should catch any exception
 * inside {@link #send(SendRequest)}, converting it into a {@link SendResponse}
 * without propagating runtime exceptions; otherwise retry logic will be skipped.</p>
 *
 * @author VirtualGemini
 * @since 1.0
 */
public interface IEmailChannel {

    /**
     * Returns the channel name, used for logging, metrics and routing.
     *
     * @return channel name, never {@code null}
     */
    String name();

    /**
     * Sends an e-mail synchronously.
     * <p>Implementations must handle all exceptions internally and return
     * a failure {@link SendResponse}; any uncaught exception will bypass
     * upstream retry mechanisms.</p>
     *
     * @param request send request
     * @return send response, never {@code null}
     */
    SendResponse send(SendRequest request);

    /**
     * Determines whether the given error code is retryable.
     * <p>Defaults to {@link #DEFAULT_RETRYABLE}; subclasses may override
     * for custom policies.</p>
     *
     * @param code error code, typically from {@link SendResponse#getErrorCode()}
     * @return {@code true} if retryable, {@code false} otherwise
     */
    default boolean retryable(int code) {
        return DEFAULT_RETRYABLE.contains(code);
    }

    /**
     * Read-only whitelist of retryable error codes.
     * <ul>
     *   <li>421 - Service not available (temporary)</li>
     *   <li>451 - Server internal error</li>
     *   <li>452 - Insufficient system storage</li>
     * </ul>
     */
    Set<Integer> DEFAULT_RETRYABLE = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(421, 451, 452))
    );
}

