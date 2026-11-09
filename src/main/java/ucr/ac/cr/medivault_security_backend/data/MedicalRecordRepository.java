package ucr.ac.cr.medivault_security_backend.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ucr.ac.cr.medivault_security_backend.domain.MedicalRecord;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientId(Long patientId);

    @Query("SELECT r FROM MedicalRecord r "
            + "JOIN FETCH r.patient "
            + "JOIN FETCH r.assignedDoctor "
            + "WHERE r.id = :id")
    Optional<MedicalRecord> findByIdWithDetails(@Param("id") Long id);
}
