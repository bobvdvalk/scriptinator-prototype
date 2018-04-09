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
package io.chapp.scriptinator.libraries;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.libraries.core.ClosableContext;
import io.chapp.scriptinator.libraries.core.ObjectConverter;
import io.chapp.scriptinator.libraries.core.ScriptinatorExecutionException;
import io.chapp.scriptinator.libraries.http.HttpLibrary;
import io.chapp.scriptinator.libraries.test.AssertLibrary;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.SecretService;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScriptLibrary {
    private static final Map<String, Function<ScriptLibrary, ?>> LIBRARIES = new HashMap<>();

    static {
        LIBRARIES.put("HTTP", lib -> new HttpLibrary(lib.converter, lib.closableContext));
        LIBRARIES.put("Assert", lib -> new AssertLibrary(new ObjectMapper()));
    }

    private final JobService jobService;
    private final Job job;
    private final ScriptService scriptService;
    private final ObjectConverter converter;
    private final ClosableContext closableContext;
    private final SecretService secretService;

    public ScriptLibrary(JobService jobService, Job job, ScriptService scriptService, ClosableContext closableContext, SecretService secretService) {
        this.jobService = jobService;
        this.job = job;
        this.converter = new ObjectConverter(new ObjectMapper());
        this.closableContext = closableContext;
        this.scriptService = scriptService;
        this.secretService = secretService;
    }

    public Object library(String name) {
        Function<ScriptLibrary, ?> builder = LIBRARIES.get(name);
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
        // Get the string to be logged.
        String logString = Arrays.stream(values)
                .map(this::getStringValue)
                .collect(Collectors.joining(" "));

        // Filter out any secrets.
        logString = secretService.filterSecrets(
                job.getScript().getProject().getId(),
                logString
        );

        job.log(level, logString);
        jobService.update(job);
    }

    private String getStringValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return argToString(value);
    }

    public void run(String fullName) {
        run(fullName, null);
    }

    public void run(String fullName, Object argument) {
        String[] nameParts = StringUtils.split(fullName, '/');
        String projectName = job.getScript().getProject().getName();
        String scriptName = "";

        switch (nameParts.length) {
            case 1:
                scriptName = nameParts[0];
                break;
            case 2:
                projectName = nameParts[0];
                scriptName = nameParts[1];
                break;
            default:
                throw new IllegalArgumentException("It seems like '" + fullName + "' is not a valid script name.");
        }

        // Get the script.
        try {
            Script script = scriptService.getOwnedBy(
                    job.getScript().getProject().getOwner().getUsername(),
                    projectName,
                    scriptName
            );

            scriptService.run(script, job, argToString(argument));
        } catch (NoSuchElementException e) {
            throw new ScriptinatorExecutionException("Could not start the script '" + fullName + "' because it could not be found.");
        }
    }

    private String argToString(Object argument) {
        if (argument == null) {
            return null;
        }

        return NativeJSON.stringify(null, argument, null, null).toString();
    }

    public Object argument() {
        if (job.getArgument() == null) {
            return null;
        }

        return JSONFunctions.parse(job.getArgument(), null);
    }

    public String secret(String name) {
        return secretService.getValue(job.getScript().getProject().getId(), name);
    }
}
