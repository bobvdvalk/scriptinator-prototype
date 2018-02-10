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

import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class MessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    private final JobService jobService;
    private final ScriptExecutor scriptExecutor;

    public MessageReceiver(JobService jobService, ScriptExecutor scriptExecutor) {
        this.jobService = jobService;
        this.scriptExecutor = scriptExecutor;
    }

    public void executeJob(long jobId) {
        Job job = getJob(jobId);
        if (job == null) {
            return;
        }

        scriptExecutor.execute(job);
    }

    private Job getJob(long jobId) {
        try {
            return jobService.get(jobId);
        } catch (NoSuchElementException e) {
            // The job no longer exists
            LOGGER.debug("The job was not found", e);
            return null;
        }
    }
}
