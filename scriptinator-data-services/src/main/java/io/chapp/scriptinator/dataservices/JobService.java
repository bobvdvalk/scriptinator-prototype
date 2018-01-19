package io.chapp.scriptinator.dataservices;


import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.repositories.JobRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class JobService extends AbstractEntityService<Job, JobRepository> {
    private final RabbitTemplate rabbitTemplate;

    public JobService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Page<Job> get(int actionId, Pageable pageable) {
        return getRepository().findAllByActionId(actionId, pageable);
    }

    @Override
    public Job create(Job entity) {
        // Here we don't call super because there is no need to add this to the audit log
        entity.setId(null);
        entity.changeStatus(Job.Status.QUEUED);
        entity = getRepository().save(entity);

        // Add this job to the queue so that a worker picks it up
        rabbitTemplate.convertAndSend(Job.NEW_JOB_QUEUE, Integer.toString(entity.getId()));

        return entity;
    }
}
