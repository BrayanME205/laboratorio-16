package ucr.ac.cr.medivault_security_backend.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucr.ac.cr.medivault_security_backend.controller.dto.MedicalRecordRequestDto;
import ucr.ac.cr.medivault_security_backend.controller.dto.MedicalRecordResponseDto;
import ucr.ac.cr.medivault_security_backend.data.MedicalRecordRepository;
import ucr.ac.cr.medivault_security_backend.data.UserRepository;
import ucr.ac.cr.medivault_security_backend.domain.MedicalRecord;
import ucr.ac.cr.medivault_security_backend.domain.Role;
import ucr.ac.cr.medivault_security_backend.domain.User;
import ucr.ac.cr.medivault_security_backend.exception.AccessDeniedSecurityException;
import ucr.ac.cr.medivault_security_backend.exception.ResourceNotFoundException;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(MedicalRecordRepository recordRepository,
                                UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponseDto findById(Long recordId, String currentUsername) {
        MedicalRecord record = recordRepository.findByIdWithDetails(recordId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record does not exist: " + recordId));

        User requester = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedSecurityException("Unrecognized user."));

        validateResourceOwnership(record, requester);

        return mapToResponseDto(record);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordResponseDto> listOwnRecords(String currentUsername) {
        User requester = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AccessDeniedSecurityException("Unrecognized user."));

        return recordRepository.findByPatientId(requester.getId())
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Transactional
    public MedicalRecordResponseDto register(MedicalRecordRequestDto dto, String doctorUsername) {
        User doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new AccessDeniedSecurityException("Doctor not registered."));

        if (doctor.getRole() != Role.ROLE_DOCTOR && doctor.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedSecurityException(
                    "Only authorized doctors can register medical records.");
        }

        User patient = userRepository.findById(dto.patientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient does not exist: " + dto.patientId()));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setAssignedDoctor(doctor);
        record.setRecordCode(dto.recordCode());
        record.setMainDiagnosis(dto.mainDiagnosis());
        record.setClinicalNotes(dto.clinicalNotes());
        record.setConfidentialityLevel(dto.confidentialityLevel());

        return mapToResponseDto(recordRepository.save(record));
    }

    private void validateResourceOwnership(MedicalRecord record, User requester) {
        if (requester.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (requester.getRole() == Role.ROLE_PACIENTE) {
            if (!record.getPatient().getId().equals(requester.getId())) {
                throw new AccessDeniedSecurityException("IDOR violation: access denied.");
            }
            return;
        }

        if (requester.getRole() == Role.ROLE_DOCTOR) {
            if (!record.getAssignedDoctor().getId().equals(requester.getId())) {
                throw new AccessDeniedSecurityException("BOLA violation: not assigned.");
            }
        }
    }

    private MedicalRecordResponseDto mapToResponseDto(MedicalRecord record) {
        return new MedicalRecordResponseDto(
                record.getId(),
                record.getRecordCode(),
                record.getPatient().getFullName(),
                record.getAssignedDoctor().getFullName(),
                record.getMainDiagnosis(),
                record.getClinicalNotes(),
                record.getConfidentialityLevel(),
                record.getRegisteredAt()
        );
    }
}