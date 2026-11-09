package ucr.ac.cr.medivault_security_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ucr.ac.cr.medivault_security_backend.business.SecurityAuditorService;
import ucr.ac.cr.medivault_security_backend.controller.dto.AuditLogResponseDto;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final SecurityAuditorService auditor;

    public AuditLogController(SecurityAuditorService auditor) {
        this.auditor = auditor;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponseDto>> list() {
        return ResponseEntity.ok(auditor.findAllEvents());
    }
}