package ucr.ac.cr.medivault_security_backend.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "AUDIT_LOGS")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "ip_origen", nullable = false, length = 45)
    private String originIp;

    @Column(name = "accion", nullable = false, length = 50)
    private String action;

    @Column(name = "recurso_solicitado", nullable = false)
    private String requestedResource;

    @Column(name = "codigo_estado", nullable = false)
    private Integer statusCode;

    @Column(name = "detalle_evento")
    private String eventDetail;

    @Column(name = "fecha_evento", insertable = false, updatable = false)
    private LocalDateTime eventDate;

    protected AuditLog() {}

    public AuditLog(String username, String originIp, String action,
                    String requestedResource, Integer statusCode, String eventDetail) {
        this.username = username;
        this.originIp = originIp;
        this.action = action;
        this.requestedResource = requestedResource;
        this.statusCode = statusCode;
        this.eventDetail = eventDetail;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getOriginIp() { return originIp; }
    public String getAction() { return action; }
    public String getRequestedResource() { return requestedResource; }
    public Integer getStatusCode() { return statusCode; }
    public String getEventDetail() { return eventDetail; }
    public LocalDateTime getEventDate() { return eventDate; }
}
