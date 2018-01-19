package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.ActionService;
import io.chapp.scriptinator.dataservices.ProjectService;
import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("projects")
public class ProjectsRestApiController extends AbstractRestApiController<Project, ProjectService> {
    private final ActionService actionService;

    public ProjectsRestApiController(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping(value = "{projectId}/actions", produces = "application/json")
    public Page<Action> list(@PathVariable("projectId") int projectId,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return actionService.get(projectId, new PageRequest(page - 1, size));
    }

    @PostMapping(value = "{projectId}/actions", consumes = "application/json", produces = "application/json")
    public Action addAction(@PathVariable("projectId") int projectId,
                            @RequestBody Action action) {
        action.setProject(getService().get(projectId));
        return actionService.create(action);
    }
}
