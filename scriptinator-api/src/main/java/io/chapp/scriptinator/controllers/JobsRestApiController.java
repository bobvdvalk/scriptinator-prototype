package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.JobService;
import io.chapp.scriptinator.model.Job;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jobs")
public class JobsRestApiController extends AbstractRestApiController<Job, JobService> {
}
