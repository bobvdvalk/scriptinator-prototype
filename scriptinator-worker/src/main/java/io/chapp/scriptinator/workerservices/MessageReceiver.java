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

import io.chapp.scriptinator.repositories.JobRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {
    private final JobRepository jobRepository;
    private final ScriptExecutor scriptExecutor;

    public MessageReceiver(JobRepository jobRepository, ScriptExecutor scriptExecutor) {
        this.jobRepository = jobRepository;
        this.scriptExecutor = scriptExecutor;
    }


    public void executeJob(long jobId) {
        jobRepository.findOne(jobId)
                .ifPresent(scriptExecutor::execute);
    }
}
