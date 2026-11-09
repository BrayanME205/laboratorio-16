package ucr.ac.cr.medivault_security_backend.controller.dto;

import java.time.LocalDateTime;

public record MedicalRecordResponseDto(
        Long id,
        String recordCode,
        String patientName,
        String doctorName,
        String mainDiagnosis,
        String clinicalNotes,
        String confidentialityLevel,
        LocalDateTime registeredAt
) {}