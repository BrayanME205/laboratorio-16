package ucr.ac.cr.medivault_security_backend.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucr.ac.cr.medivault_security_backend.controller.dto.AuditLogResponseDto;
import ucr.ac.cr.medivault_security_backend.data.AuditLogRepository;
import ucr.ac.cr.medivault_security_backend.domain.AuditLog;

@Service
public class SecurityAuditorService {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditorService.class);

    private final AuditLogRepository auditLogRepository;

    public SecurityAuditorService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    public void logIdorAttempt(String username, String originIp, String resource, String detail) {
        persist(username, originIp, "IDOR_ATTEMPT_BLOCKED", resource, 403, detail);
    }

    @Async
    public void logLoginFailed(String username, String originIp, String resource, String detail) {
        persist(username, originIp, "LOGIN_FAILED", resource, 401, detail);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponseDto> findAllEvents() {
        return auditLogRepository.findAllByOrderByIdDesc()
                .stream()
                .map(entry -> new AuditLogResponseDto(
                        entry.getId(),
                        entry.getUsername(),
                        entry.getOriginIp(),
                        entry.getAction(),
                        entry.getRequestedResource(),
                        entry.getStatusCode(),
                        entry.getEventDetail(),
                        entry.getEventDate()))
                .toList();
    }

    private void persist(String username, String originIp, String action,
                         String resource, int statusCode, String detail) {
        try {
            auditLogRepository.save(new AuditLog(
                    truncate(username, 50),
                    truncate(originIp, 45),
                    action,
                    truncate(resource, 255),
                    statusCode,
                    truncate(detail, 255)));
        } catch (RuntimeException ex) {
            log.error("Failed to persist audit event {}", action, ex);
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
