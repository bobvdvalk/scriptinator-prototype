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
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.SecretService;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.chapp.scriptinator.libraries.core.EncodeUtils.toNativeJsonOrNull;

public class BuiltInLogFunction implements BuiltIn {
    private final String level;
    private final JobService jobService;
    private final Job job;
    private final SecretService secretService;

    public BuiltInLogFunction(String level, JobService jobService, Job job, SecretService secretService) {
        this.level = level;
        this.jobService = jobService;
        this.job = job;
        this.secretService = secretService;
    }

    @Override
    public Object run(Object... args) {
        // Get the string to be logged.
        String logString = Arrays.stream(args)
                .map(this::getStringValue)
                .collect(Collectors.joining(" "));

        // Filter out any secrets.
        logString = secretService.filterSecrets(
                job.getScript().getProject().getId(),
                logString
        );

        job.log(level, logString);
        jobService.update(job);
        return null;
    }

    private String getStringValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return toNativeJsonOrNull(value);
    }
}
