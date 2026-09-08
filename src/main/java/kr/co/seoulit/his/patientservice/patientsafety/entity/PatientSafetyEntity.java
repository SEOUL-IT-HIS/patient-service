package kr.co.seoulit.his.patientservice.patientsafety.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "PATIENT_SAFETY_INFO")
public class PatientSafetyEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "SAFETY_INFO_ID",
            nullable = false,
            updatable = false,
            length = 36,
            columnDefinition = "VARCHAR2(36 BYTE)")
    private UUID safetyInfoId;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "PATIENT_ID",
            nullable = false,
            updatable = false,
            length = 36,
            columnDefinition = "VARCHAR2(36 BYTE)")
    private UUID patientId;

    @Column(
            name = "SAFETY_NOTE",
            nullable = false,
            length = 2000,
            columnDefinition = "VARCHAR2(2000 BYTE)")
    private String safetyNote;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "ACTIVE_YN",
            nullable = false,
            length = 1,
            columnDefinition = "CHAR(1 BYTE)")
    private String activeYn = "Y";

@Column(
            name = "CREATED_AT",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    public static PatientSafetyEntity create(
            UUID patientId,
            String safetyNote) {

        PatientSafetyEntity entity = new PatientSafetyEntity();
        entity.patientId = patientId;
        entity.safetyNote = safetyNote;
        return entity;
    }

    public void updateSafetyNote(String safetyNote) {
        this.safetyNote = safetyNote;
    }

    public void deactivate() {
        this.activeYn = "N";
    }

    public boolean isActive() {
        return "Y".equals(this.activeYn);
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}