package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.ActionService;
import io.chapp.scriptinator.dataservices.JobService;
import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("actions")
public class ActionsRestApiController extends AbstractRestApiController<Action, ActionService> {
    private final JobService jobService;

    public ActionsRestApiController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping(value = "{actionId}/jobs", produces = "application/json")
    public Page<Job> list(@PathVariable("actionId") int actionId,
                          @RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size) {
        return jobService.get(actionId, new PageRequest(page - 1, size));
    }

    @PostMapping(value = "{actionId}/jobs", produces = "application/json")
    public Job list(@PathVariable("actionId") int actionId,
                    @RequestBody Job job) {
        Action action = get(actionId);
        job.setAction(action);
        return jobService.create(job);
    }
}
