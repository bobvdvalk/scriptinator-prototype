package io.chapp.scriptinator.repositories;

import io.chapp.scriptinator.model.AbstractEntity;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface AbstractEntityRepository<E extends AbstractEntity> extends PagingAndSortingRepository<E, Integer> {
}
