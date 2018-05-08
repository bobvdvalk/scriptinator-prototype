/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.libraries.builtins;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.Job;

import java.io.IOException;
import java.util.function.Supplier;

public class ArgumentFunction implements Supplier<Object> {
    private final Job job;
    private final ObjectMapper objectMapper;

    public ArgumentFunction(Job job, ObjectMapper objectMapper) {
        this.job = job;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object get() {
        if (job.getArgument() == null) {
            return null;
        }

        try {
            return objectMapper.readValue(job.getArgument(), Object.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to retrieve argument... Please contact a system administrator", e);
        }
    }
}
