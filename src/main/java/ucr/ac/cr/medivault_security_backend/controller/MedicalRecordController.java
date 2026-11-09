package ucr.ac.cr.medivault_security_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ucr.ac.cr.medivault_security_backend.business.MedicalRecordService;
import ucr.ac.cr.medivault_security_backend.controller.dto.MedicalRecordRequestDto;
import ucr.ac.cr.medivault_security_backend.controller.dto.MedicalRecordResponseDto;

@RestController
@RequestMapping("/api/v1/expedientes")
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    public MedicalRecordController(MedicalRecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponseDto> getById(@PathVariable Long id,
                                                            Authentication authentication) {
        return ResponseEntity.ok(recordService.findById(id, authentication.getName()));
    }

    @GetMapping("/mis-expedientes")
    public ResponseEntity<List<MedicalRecordResponseDto>> getMyRecords(
            Authentication authentication) {
        return ResponseEntity.ok(recordService.listOwnRecords(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponseDto> create(
            @Valid @RequestBody MedicalRecordRequestDto request,
            Authentication authentication) {
        MedicalRecordResponseDto created =
                recordService.register(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
