/*
 * Copyright © 2018 Thomas Biesaart (thomas.biesaart@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.chapp.scriptinator.services;

import io.chapp.scriptinator.model.AbstractEntity;
import io.chapp.scriptinator.repositories.AbstractEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.NoSuchElementException;
import java.util.Optional;

public class AbstractEntityService<E extends AbstractEntity, R extends AbstractEntityRepository<E>> {
    private R repository;

    /**
     * Get an entity by id from this repository.
     *
     * @param id the id
     * @return the entity
     * @throws NoSuchElementException if the entity was not found
     */
    public E get(long id) {
        E entity = repository.findOne(id);
        if (entity == null) {
            throw new NoSuchElementException(Long.toString(id));
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

    /**
     * Delete an entity.
     *
     * @param id the id of the entity to delete
     */
    public void delete(long id) {
        repository.delete(id);
    }

    /**
     * Find an entity.
     *
     * @param id the id
     * @return an optional containing the entity
     */
    public Optional<E> findOne(long id) {
        return Optional.ofNullable(repository.findOne(id));
    }

    protected R getRepository() {
        return repository;
    }

    @Autowired
    void setRepository(R repository) {
        this.repository = repository;
    }
}
