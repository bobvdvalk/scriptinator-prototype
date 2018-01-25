package io.chapp.scriptinator.dataservices;

import io.chapp.scriptinator.model.AbstractEntity;
import io.chapp.scriptinator.model.Audit;
import io.chapp.scriptinator.repositories.AuditRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void created(AbstractEntity entity) {
        log(entity, "was created");
    }

    public void updated(AbstractEntity entity) {
        log(entity, "was updated");
    }

    public void deleted(AbstractEntity entity) {
        log(entity, "was deleted");
    }

    public void log(AbstractEntity entity, String message) {
        Audit audit = new Audit();
        audit.setSubjectName(entity.toString());
        audit.setSubjectLink(entity.getClass().getSimpleName().toLowerCase() + "/" + entity.getId());
        audit.setMessage(message);
        audit.setTime(new Date());
        auditRepository.save(audit);
    }

    public Page<Audit> getLast(int count) {
        return auditRepository.findAll(new PageRequest(0, count, new Sort(Sort.Direction.DESC, "time")));
    }
}
