package io.chapp.scriptinator.controller;

import io.chapp.scriptinator.model.JobDtos;
import io.chapp.scriptinator.services.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final Logger logger = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @RequestMapping("/")
    @ResponseBody
    public JobDtos getJobById(@RequestParam("job-id") int jobId) {
        return JobDtos.convert(jobService.get(jobId));
    }
}