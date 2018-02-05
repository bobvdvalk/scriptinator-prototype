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
package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.repositories.JobRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobService extends AbstractEntityService<Job, JobRepository> {
    private final RabbitTemplate rabbitTemplate;

    public JobService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Page<Job> get(long scriptId, PageRequest pageRequest) {
        return getRepository().findByScriptId(scriptId, pageRequest);
    }

    @Override
    public Job create(Job entity) {
        Job job = changeStatus(entity, Job.Status.QUEUED);

        job.setDisplayName(job.getDisplayName() + " #" + job.getId());
        job = super.update(entity);

        rabbitTemplate.convertAndSend(
                Job.EXECUTE_JOB_QUEUE,
                job.getId()
        );

        return job;
    }

    public Job changeStatus(Job job, Job.Status status) {
        job.setStatus(status);
        switch (status) {
            case QUEUED:
                job.setQueuedTime(new Date());
                break;
            case RUNNING:
                job.setStartedTime(new Date());
                break;
            case FAILED:
            case FINISHED:
                job.setFinishedTime(new Date());
                break;
        }
        if (job.getId() == null) {
            return super.create(job);
        }
        return update(job);
    }
}
