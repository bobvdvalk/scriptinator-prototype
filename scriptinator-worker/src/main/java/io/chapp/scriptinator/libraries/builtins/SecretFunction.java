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

import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.services.SecretService;

import java.util.function.Function;

public class SecretFunction implements Function<String, String> {
    private final SecretService secretService;
    private final Job job;

    public SecretFunction(SecretService secretService, Job job) {
        this.secretService = secretService;
        this.job = job;
    }

    @Override
    public String apply(String name) {
        return secretService.getValue(job.getScript().getProject().getId(), name);
    }
}
