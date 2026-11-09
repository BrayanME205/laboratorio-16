package ucr.ac.cr.medivault_security_backend.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDto(
        @NotNull(message = "Patient ID is required.")
        @Positive(message = "Patient ID must be positive.")
        Long patientId,

        @NotBlank(message = "Record code is required.")
        @Pattern(regexp = "^EXP-\\d{4}-\\d{3,6}$", message = "Format: EXP-YYYY-NNN.")
        String recordCode,

        @NotBlank(message = "Diagnosis is required.")
        @Size(min = 5, max = 255, message = "Diagnosis: 5 to 255 characters.")
        String mainDiagnosis,

        @NotBlank(message = "Clinical notes are required.")
        @Size(min = 10, max = 4000, message = "Notes: 10 to 4000 characters.")
        String clinicalNotes,

        @NotBlank(message = "Confidentiality level is required.")
        @Pattern(regexp = "^(BAJO|MEDIO|ALTO)$", message = "Allowed values: BAJO, MEDIO, ALTO.")
        String confidentialityLevel
) {}
