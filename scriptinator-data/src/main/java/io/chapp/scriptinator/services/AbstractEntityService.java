package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.AbstractEntity;
import io.chapp.scriptinator.repositories.AbstractEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.NoSuchElementException;

public class AbstractEntityService<E extends AbstractEntity, R extends AbstractEntityRepository<E>> {
    private R repository;


    /**
     * Get an entity by id from this repository.
     *
     * @param id the id
     * @return the entity
     * @throws NoSuchElementException if the entity was not found
     */
    public E get(int id) {
        E entity = repository.findOne(id);
        if (entity == null) {
            throw new NoSuchElementException(Integer.toString(id));
        }
        return entity;
    }

    /**
     * Get all entities in a paginated set.
     *
     * @param pageRequest the paging settings
     * @return the page
     */
    public Page<E> get(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    /**
     * Create a new entity.
     *
     * @param entity the entity
     * @return a reference to the updated entity. Use this instead of the entity passed as the argument.
     */
    public E create(E entity) {
        entity.setId(null);
        return repository.save(entity);
    }

    /**
     * Save changes to an already known entity.
     *
     * @param entity the entity
     * @return a reference to the updated entity. Use this instead of the entity passed as the argument.
     * @throws IllegalArgumentException if the entity does not have an id
     */
    public E update(E entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Cannot save an entity without an id. Use #create(entity) instead");
        }
        return repository.save(entity);
    }

    protected R getRepository() {
        return repository;
    }

    @Autowired
    void setRepository(R repository) {
        this.repository = repository;
    }
}
