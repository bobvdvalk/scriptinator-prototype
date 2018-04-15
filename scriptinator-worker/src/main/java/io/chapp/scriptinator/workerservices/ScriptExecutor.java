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
package io.chapp.scriptinator.workerservices;

import io.chapp.scriptinator.libraries.ScriptLibrary;
import io.chapp.scriptinator.libraries.core.ClosableContext;
import io.chapp.scriptinator.libraries.core.ScriptinatorExecutionException;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.services.JobService;
import io.chapp.scriptinator.services.ScriptService;
import io.chapp.scriptinator.services.SecretService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

import javax.script.*;

@Service
public class ScriptExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptExecutor.class);

    private final ObjectFactory<ScriptEngine> scriptEngineFactory;
    private final JobService jobService;
    private final ScriptService scriptService;
    private final SecretService secretService;

    public ScriptExecutor(ObjectFactory<ScriptEngine> scriptEngineFactory, JobService jobService, ScriptService scriptService, SecretService secretService) {
        this.scriptEngineFactory = scriptEngineFactory;
        this.jobService = jobService;
        this.scriptService = scriptService;
        this.secretService = secretService;
    }

    public void execute(Job job) {
        // Create engine
        ScriptEngine engine = scriptEngineFactory.getObject();

        try (ClosableContext context = new ClosableContext()) {
            ScriptLibrary scriptLibrary = new ScriptLibrary(jobService, job, scriptService, context, secretService);
            Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            scriptLibrary.apply(bindings);

            execute(engine, job);
        }
    }

    @SuppressWarnings("squid:S1181") // This is a third party script. We should catch everything.
    private void execute(ScriptEngine engine, Job job) {
        CompiledScript script = compile(engine, job);
        if (script == null) {
            return;
        }

        try {
            jobService.changeStatus(job, Job.Status.RUNNING);
            script.eval();
            jobService.changeStatus(job, Job.Status.FINISHED);
        } catch (ScriptinatorExecutionException e) {
            job.log("FATAL", e.getMessage());
            jobService.changeStatus(job, Job.Status.FAILED);
        } catch (Throwable e) {
            LOGGER.error("Unknown fatal error", e);
            job.log("FATAL", "Oops, something unexpected happened while running your script: " + e.getMessage());
            jobService.changeStatus(job, Job.Status.FAILED);
            LOGGER.error("An unexpected exception occurred while running a script.", e);
        }
    }

    private CompiledScript compile(ScriptEngine engine, Job job) {
        try {
            return ((Compilable) engine).compile(job.getScript().getCode());
        } catch (ScriptException e) {
            job.log("COMPILE", "Something didn't look right on line " + e.getLineNumber() + ": " + e.getMessage());
            jobService.changeStatus(job, Job.Status.FAILED);
            return null;
        }
    }
}
