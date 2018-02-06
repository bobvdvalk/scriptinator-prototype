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
import io.chapp.scriptinator.libraries.core.ClosableContext;
import io.chapp.scriptinator.libraries.core.ObjectConverter;
import io.chapp.scriptinator.libraries.http.HttpLibrary;
import io.chapp.scriptinator.libraries.test.AssertLibrary;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ProjectService;
import io.chapp.scriptinator.services.ScriptService;
import jdk.nashorn.internal.objects.NativeJSON;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private final ProjectService projectService;
    private final ObjectConverter converter;
    private final ClosableContext closableContext;

    public ScriptLibrary(JobService jobService, Job job, ScriptService scriptService, ProjectService projectService, ClosableContext closableContext) {
        this.jobService = jobService;
        this.job = job;
        this.converter = new ObjectConverter(new ObjectMapper());
        this.closableContext = closableContext;
        this.scriptService = scriptService;
        this.projectService = projectService;
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
        job.log(
                level,
                Arrays.stream(values)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "))
        );
        jobService.update(job);
    }

    public Long run(String fullName) {
        return run(fullName, null);
    }

    public Long run(String fullName, Object argument) {
        String projectName = null;
        String scriptName = fullName;

        // Parse the name parts: (project/)?(script)
        String[] parts = StringUtils.split(fullName, "/");
        if (parts != null) {
            if (parts.length == 2) {
                projectName = parts[0];
                scriptName = parts[1];
            } else if (parts.length > 2) {
                return null;
            }
        }

        // Get the project.
        Project project = job.getScript().getProject();
        if (projectName != null) {
            project = projectService.find(projectName).orElse(null);
        }
        if (project == null) {
            return null;
        }

        // Get the script.
        Optional<Script> script = scriptService.get(project.getId(), scriptName);
        if (!script.isPresent()) {
            return null;
        }

        return scriptService.run(script.get(), job, argToString(argument)).getId();
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
}
