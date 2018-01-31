/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
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
package io.chapp.scriptinator.libraries;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.libraries.http.HttpLibrary;
import io.chapp.scriptinator.libraries.test.AssertLibrary;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.workerservices.ObjectConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScriptLibrary {
    private static final Map<String, Function<ScriptLibrary, ?>> libraries = new HashMap<>();

    static {
        libraries.put("HTTP", lib -> new HttpLibrary(lib.converter));
        libraries.put("Assert", lib -> new AssertLibrary(new ObjectMapper()));
    }

    private final JobService jobService;
    private final Job job;
    private final ObjectConverter converter;

    public ScriptLibrary(JobService jobService, Job job, ObjectConverter converter) {
        this.jobService = jobService;
        this.job = job;
        this.converter = converter;
    }

    public Object library(String name) {
        Function<ScriptLibrary, ?> builder = libraries.get(name);
        if (builder == null) {
            return null;
        }
        return builder.apply(this);
    }

    public void debug(Object... values) {
        log("DEBUG", values);
    }

    public void info(Object... values) {
        log("INFO", values);
    }

    public void warn(Object... values) {
        log("WARN", values);
    }

    public void error(Object... values) {
        log("ERROR", values);
    }


    private void log(String level, Object[] values) {
        job.log(
                level,
                Arrays.stream(values)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))
        );
        jobService.update(job);
    }
}
