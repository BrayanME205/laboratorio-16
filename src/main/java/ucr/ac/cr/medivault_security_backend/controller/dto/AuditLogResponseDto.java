package ucr.ac.cr.medivault_security_backend.controller.dto;

import java.time.LocalDateTime;

public record AuditLogResponseDto(
        Long id,
        String username,
        String originIp,
        String action,
        String requestedResource,
        Integer statusCode,
        String eventDetail,
        LocalDateTime eventDate
) {}
