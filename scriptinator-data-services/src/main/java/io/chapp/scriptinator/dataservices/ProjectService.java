package io.chapp.scriptinator.dataservices;

import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends AbstractEntityService<Project, ProjectRepository> {
    private final ActionService actionService;

    public ProjectService(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public Project create(Project entity) {
        entity = super.create(entity);

        // After creating a new project, add the default action.
        Action action = new Action();
        action.setProject(entity);
        action.setDisplayName("Run index.js");
        action.setName("run");
        action.setScript("index.js");

        entity.getActions().add(
                actionService.create(action)
        );

        return entity;
    }
}
