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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.chapp.scriptinator.model.*;
import io.chapp.scriptinator.repositories.ScriptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class ScriptService extends AbstractEntityService<Script, ScriptRepository> {
    private final JobService jobService;
    private final WebhookService webhookService;
    private final ObjectMapper objectMapper;

    public ScriptService(JobService jobService, WebhookService webhookService, ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.webhookService = webhookService;
        this.objectMapper = objectMapper;
    }

    public Optional<Script> findOwnedBy(String username, long projectId, long scriptId) {
        return getRepository().findByProjectOwnerUsernameAndProjectIdAndId(username, projectId, scriptId);
    }

    public Optional<Script> findOwnedByPrincipal(long projectId, long scriptId) {
        return findOwnedBy(DataServiceUtils.getPrincipalName(), projectId, scriptId);
    }

    public Optional<Script> findOwnedBy(String username, String projectName, String scriptName) {
        return getRepository().findByProjectOwnerUsernameAndProjectNameAndName(
                username,
                projectName,
                scriptName
        );
    }

    public Optional<Script> findOwnedByPrincipal(String projectName, String scriptName) {
        return findOwnedBy(DataServiceUtils.getPrincipalName(), projectName, scriptName);
    }

    public Script getOwnedByPrincipal(long projectId, long scriptId) {
        return findOwnedByPrincipal(projectId, scriptId).orElseThrow(() -> noSuchElement(scriptId));
    }

    public Script getOwnedByPrincipal(long scriptId) {
        return getRepository().findByProjectOwnerUsernameAndId(
                DataServiceUtils.getPrincipalName(),
                scriptId
        ).orElseThrow(() -> noSuchElement(scriptId));
    }

    public Script getOwnedBy(String username, String projectName, String scriptName) {
        return findOwnedBy(username, projectName, scriptName)
                .orElseThrow(() -> noSuchElement(projectName + "/" + scriptName));
    }

    public Script getOwnedByPrincipal(String projectName, String scriptName) {
        return getOwnedBy(DataServiceUtils.getPrincipalName(), projectName, scriptName);
    }

    public Page<Script> findAllForProjectOwnedBy(String username, String projectName, Pageable pageable) {
        return getRepository().findAllByProjectOwnerUsernameAndProjectName(
                username,
                projectName,
                pageable
        );
    }

    public Page<Script> findAllForProjectOwnedByPrincipal(String projectName, Pageable pageable) {
        return findAllForProjectOwnedBy(DataServiceUtils.getPrincipalName(), projectName, pageable);
    }

    public Page<Script> findAllOwnedBy(String username, Pageable pageable) {
        return getRepository().findAllByProjectOwnerUsername(username, pageable);
    }

    public Page<Script> findAllOwnedByPrincipal(Pageable pageable) {
        return findAllOwnedBy(DataServiceUtils.getPrincipalName(), pageable);
    }

    public void deleteIfOwnedBy(String username, long scriptId) {
        getRepository().deleteAllByProjectOwnerUsernameAndId(username, scriptId);
    }

    public void deleteIfOwnedByPrincipal(long scriptId) {
        deleteIfOwnedBy(DataServiceUtils.getPrincipalName(), scriptId);
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
     * Run a script, optionally triggered by a job.
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

    /**
     * Run a script with an argument. The argument will be parsed to a JSON string.
     *
     * @param script   The script to run.
     * @param argument The argument object passed to the script.
     * @return The created job.
     */
    public Job run(Script script, Object argument) {
        return run(script, null, stringify(argument));
    }

    /**
     * Run a script triggered by a schedule.
     *
     * @param script The script to run.
     */
    public void runScheduled(Script script, Schedule schedule) {
        Job job = createJob(script, schedule.getArgument());
        job.setTriggeredByScheduleId(schedule.getId());
        jobService.create(job);
    }

    /**
     * Run a script triggered by a webhook.
     *
     * @param webhookUuid The uuid of the webhook.
     * @param argument    The argument from the request.
     */
    public void runWebhooked(String webhookUuid, WebhookArgument argument) {
        // Get and update the webhook.
        Webhook webhook = webhookService.getByUuid(webhookUuid);
        webhook.setLastCall(Date.from(Instant.now(Clock.systemDefaultZone())));
        webhookService.update(webhook);

        // Get the script, create the job.
        Script script = getOwnedBy(webhook.getProject().getOwner().getUsername(), webhook.getProject().getName(), webhook.getScriptName());
        Job job = createJob(script, stringify(argument));
        job.setTriggeredByWebhookId(webhook.getId());
        jobService.create(job);
    }

    private String stringify(Object argument) {
        try {
            return objectMapper.writeValueAsString(argument);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not write argument as string.", e);
        }
    }

    private Job createJob(Script script, String argument) {
        Job job = new Job();
        job.setDisplayName(script.getName());
        job.setScript(script);
        job.setArgument(argument);
        return job;
    }
}
