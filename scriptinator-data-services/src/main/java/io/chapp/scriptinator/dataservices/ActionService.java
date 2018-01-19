package io.chapp.scriptinator.dataservices;

import io.chapp.scriptinator.model.Action;
import io.chapp.scriptinator.repositories.ActionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActionService extends AbstractEntityService<Action, ActionRepository> {
    public Page<Action> get(int projectId, Pageable pageable) {
        return getRepository().findAllByProjectId(projectId, pageable);
    }
}
