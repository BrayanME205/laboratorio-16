package ucr.ac.cr.medivault_security_backend.data;

import java.util.List;

import org.springframework.data.repository.Repository;

import ucr.ac.cr.medivault_security_backend.domain.AuditLog;

public interface AuditLogRepository extends Repository<AuditLog, Long> {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> findAllByOrderByIdDesc();
}