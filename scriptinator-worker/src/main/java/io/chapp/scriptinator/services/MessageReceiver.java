package io.chapp.scriptinator.services;

import io.chapp.scriptinator.ScriptinatorWorkerConfiguration;
import io.chapp.scriptinator.dataservices.AuditService;
import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.model.Job;
import io.chapp.scriptinator.model.LogEntry;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.repositories.JobRepository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class MessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiver.class);
    private final ScriptExecutor scriptExecutor;
    private final JobRepository jobRepository;
    private final ProjectDeploymentService projectDeploymentService;
    private final AuditService auditService;
    private final String workerName;

    public MessageReceiver(ScriptExecutor scriptExecutor, JobRepository jobRepository, ProjectDeploymentService projectDeploymentService, AuditService auditService, ScriptinatorWorkerConfiguration configuration) {
        this.scriptExecutor = scriptExecutor;
        this.jobRepository = jobRepository;
        this.projectDeploymentService = projectDeploymentService;
        this.auditService = auditService;
        this.workerName = configuration.getWorkerName();
    }

    public void receiveMessage(String message) {
        int jobId = Integer.parseInt(message);
        Job job = jobRepository.findOne(jobId);
        if (job == null) {
            LOGGER.error("Could not run job {} because it was not found", jobId);
            return;
        }
        try {
            job.setWorker(workerName);
            job.changeStatus(Job.Status.RUNNING);
            jobRepository.save(job);
            handle(job);
            job.changeStatus(Job.Status.DONE);
            auditService.log(job, "finished");
        } catch (Exception e) {
            LOGGER.error("Job failed", e);
            job.log(new LogEntry(e.getClass().getSimpleName() + ": " + e.getMessage()));
            job.changeStatus(Job.Status.FAILED);
            auditService.log(job, "failed");
        }

        jobRepository.save(job);
    }

    private void handle(Job job) throws IOException, ScriptException {
        Action action = job.getAction();
        Project project = action.getProject();
        Path projectPath;
        try {
            projectPath = projectDeploymentService.deploy(project);
        } catch (IOException | GitAPIException e) {
            throw new IllegalStateException("Error while checking out project: " + e.getMessage(), e);
        }

        scriptExecutor.run(project, projectPath, action.getScript());
    }
}
