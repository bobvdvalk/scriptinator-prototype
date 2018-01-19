package io.chapp.scriptinator.dataservices;

import io.chapp.scriptinator.model.AbstractEntity;
import io.chapp.scriptinator.repositories.AbstractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.NoSuchElementException;

public abstract class AbstractEntityService<T extends AbstractEntity, R extends AbstractRepository<T>> {
    private R repository;
    private AuditService auditService;

    protected R getRepository() {
        return repository;
    }

    @Autowired
    void setRepository(R repository) {
        this.repository = repository;
    }

    public Page<T> get(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public T get(int id) {
        T result = repository.findOne(id);
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    public T create(T entity) {
        entity.setId(null);
        entity = repository.save(entity);
        auditService.created(entity);
        return entity;
    }

    public T update(T entity) {
        entity = repository.save(entity);
        auditService.updated(entity);
        return entity;
    }

    public void delete(int id) {
        T entity = getRepository().findOne(id);
        repository.delete(id);
        auditService.deleted(entity);
    }

    @Autowired
    void setAuditService(AuditService auditService) {
        this.auditService = auditService;
    }

}
