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
package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.repositories.ScriptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScriptService extends AbstractEntityService<Script, ScriptRepository> {
    private final JobService jobService;

    public ScriptService(JobService jobService) {
        this.jobService = jobService;
    }

    public Script get(String projectName, String scriptName) {
        return getRepository().findOneByProjectNameAndName(projectName, scriptName)
                .orElseThrow(() -> noSuchElement(projectName + "/" + scriptName));
    }

    public Optional<Script> get(long projectId, String name) {
        return getRepository().findOneByProjectIdAndName(projectId, name);
    }

    public Page<Script> get(String projectName, PageRequest request) {
        return getRepository().findByProjectName(projectName, request);
    }

    /**
     * Run a script.
     *
     * @param script The script to run.
     * @return The created job.
     */
    public Job run(Script script) {
        return jobService.create(createJob(script, null));
    }

    /**
     * Run a script triggered by a job.
     *
     * @param script   The script to run.
     * @param trigger  The job that triggered the execution.
     * @param argument The argument passed to the script.
     * @return The created job.
     */
    public Job run(Script script, Job trigger, String argument) {
        Job job = createJob(script, argument);
        if (trigger != null) {
            job.setTriggeredByJobId(trigger.getId());
        }
        return jobService.create(job);
    }

    private Job createJob(Script script, String argument) {
        Job job = new Job();
        job.setDisplayName(script.getName());
        job.setScript(script);
        job.setArgument(argument);
        return job;
    }
}
