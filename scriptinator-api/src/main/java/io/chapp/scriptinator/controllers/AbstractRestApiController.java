package io.chapp.scriptinator.controllers;

import io.chapp.scriptinator.dataservices.AbstractEntityService;
import io.chapp.scriptinator.model.AbstractEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

public class AbstractRestApiController<T extends AbstractEntity, S extends AbstractEntityService<T, ?>> {
    private S service;

    @GetMapping(produces = "application/json")
    public Page<T> list(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "size", defaultValue = "10") int size) {
        return service.get(new PageRequest(page - 1, size));
    }

    @GetMapping(value = "{id}", produces = "application/json")
    public T get(@PathVariable("id") int id) {
        return service.get(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public T create(@RequestBody T entity) {
        return service.create(entity);
    }

    @PutMapping(value = "{id}", consumes = "application/json", produces = "application/json")
    public T update(@PathVariable("id") int id, @RequestBody T entity) {
        entity.setId(id);
        return service.update(entity);
    }

    @DeleteMapping(value = "{id}")
    public void delete(@PathVariable("id") int id) {
        service.delete(id);
    }

    protected S getService() {
        return service;
    }

    @Autowired
    void setService(S service) {
        this.service = service;
    }
}
