package ucr.ac.cr.medivault_security_backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXPEDIENTES_MEDICOS")
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_asignado_id", nullable = false)
    private User assignedDoctor;

    @Column(name = "codigo_expediente", nullable = false, unique = true, length = 30)
    private String recordCode;

    @Column(name = "diagnostico_principal", nullable = false)
    private String mainDiagnosis;

    @Column(name = "notas_clinicas", nullable = false, columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "nivel_confidencialidad", nullable = false, length = 20)
    private String confidentialityLevel;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime registeredAt;

    public MedicalRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getPatient() { return patient; }
    public void setPatient(User patient) { this.patient = patient; }

    public User getAssignedDoctor() { return assignedDoctor; }
    public void setAssignedDoctor(User assignedDoctor) { this.assignedDoctor = assignedDoctor; }

    public String getRecordCode() { return recordCode; }
    public void setRecordCode(String recordCode) { this.recordCode = recordCode; }

    public String getMainDiagnosis() { return mainDiagnosis; }
    public void setMainDiagnosis(String mainDiagnosis) { this.mainDiagnosis = mainDiagnosis; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getConfidentialityLevel() { return confidentialityLevel; }
    public void setConfidentialityLevel(String confidentialityLevel) {
        this.confidentialityLevel = confidentialityLevel;
    }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
}