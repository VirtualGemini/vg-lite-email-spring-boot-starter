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
package io.github.virtualgemini.vgliteemail.event;

import io.github.virtualgemini.vgliteemail.core.SendRequest;
import io.github.virtualgemini.vgliteemail.core.SendResponse;

/**
 * @author VirtualGemini
 * @version 1.0
 * @description: TODO
 * @createDate 2025/11/21 21:49
 */
// 事件发布器 / Event publisher

public class EmailEventPublisher {
    public void publishBefore(SendRequest req) { /* TODO */ }
    public void publishAfter(SendResponse resp) { /* TODO */ }
    public void publishError(SendRequest req, Exception ex) { /* TODO */ }
}
