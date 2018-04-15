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
import io.chapp.scriptinator.libraries.builtins.*;
import io.chapp.scriptinator.libraries.core.ClosableContext;
import io.chapp.scriptinator.libraries.core.ObjectConverter;
import io.chapp.scriptinator.libraries.http.HttpLibrary;
import io.chapp.scriptinator.libraries.test.AssertLibrary;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.SecretService;

import javax.script.Bindings;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    public void apply(Bindings bindings) {
        bindings.put("debug", new BuiltInLogFunction("DEBUG", jobService, job, secretService));
        bindings.put("info", new BuiltInLogFunction("INFO", jobService, job, secretService));
        bindings.put("warn", new BuiltInLogFunction("WARN", jobService, job, secretService));
        bindings.put("error", new BuiltInLogFunction("ERROR", jobService, job, secretService));
        bindings.put("library", new LibraryFunction(this));
        bindings.put("run", new RunFunction(job, scriptService));
        bindings.put("argument", new ArgumentFunction(job));
        bindings.put("secret", new SecretFunction(secretService, job));
    }

    public Object library(String name) {
        Function<ScriptLibrary, ?> builder = LIBRARIES.get(name);
        if (builder == null) {
            return null;
        }
        return builder.apply(this);
    }
}
