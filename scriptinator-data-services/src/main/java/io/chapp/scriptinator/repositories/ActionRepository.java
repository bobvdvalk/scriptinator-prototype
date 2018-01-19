package io.chapp.scriptinator.repositories;

import io.chapp.scriptinator.model.Action;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActionRepository extends AbstractRepository<Action> {
    Action findByProjectAndId(int projectId, int id);

    Page<Action> findAllByProjectId(int projectId, Pageable pageable);
}
