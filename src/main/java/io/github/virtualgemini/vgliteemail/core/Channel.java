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
package io.github.virtualgemini.vgliteemail.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: 发送渠道 / Sending channel
 * @createDate 2025/11/21 21:46
 */
public interface Channel {
    String name();
    SendResponse send(SendRequest request);
    boolean retryable(int errorCode);
    Set<Integer> DEFAULT_RETRYABLE = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(421, 451, 452))
    );
}
