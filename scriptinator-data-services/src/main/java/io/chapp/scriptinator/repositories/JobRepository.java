package io.chapp.scriptinator.repositories;

import io.chapp.scriptinator.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends AbstractRepository<Job> {
    Page<Job> findAllByActionId(int actionId, Pageable pageable);
}
