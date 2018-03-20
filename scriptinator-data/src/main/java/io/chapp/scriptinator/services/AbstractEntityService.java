/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
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

import java.util.NoSuchElementException;

/**
 * A base class for all entity service classes.
 *
 * @param <E> The type of the entity.
 * @param <R> The type of the entity repository.
 */
public class AbstractEntityService<E extends AbstractEntity, R extends AbstractEntityRepository<E>> {
    private R repository;

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

    protected NoSuchElementException noSuchElement(String name) {
        return new NoSuchElementException("We could not find a " + getEntityName() + " with name: " + name);
    }

    protected NoSuchElementException noSuchElement(long id) {
        return new NoSuchElementException("We could not find a " + getEntityName() + " with id: " + id);
    }

    private String getEntityName() {
        String name = getClass().getSimpleName();
        if (name.endsWith("Service")) {
            name = name.substring(0, name.length() - "Service".length());
        }
        return name.toLowerCase();
    }

    protected R getRepository() {
        return repository;
    }

    @Autowired
    void setRepository(R repository) {
        this.repository = repository;
    }
}
