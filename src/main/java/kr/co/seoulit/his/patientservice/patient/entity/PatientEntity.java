package kr.co.seoulit.his.patientservice.patient.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PATIENT")
public class PatientEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "PATIENT_ID",
            nullable = false,
            updatable = false,
            length = 36,
            columnDefinition = "VARCHAR2(36 CHAR)")
    private UUID patientId;

    @Column(name = "PATIENT_NAME")
    private String patientName;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "RESIDENT_REG_NO")
    private String residentRegNo;

    @Column(name = "GENDER_CD", nullable = false, length = 2)
    private String genderCd;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_CD", nullable = false)
    private PatientStatus statusCd = PatientStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "TEMP_PATIENT_YN", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String tempPatientYn = "N";

    @Column(name = "TEMP_REGISTER_REASON", length = 200)
    private String tempRegisterReason;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "DEATH_YN", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String deathYn = "N";

    @Column(name = "DEATH_DTM")
    private LocalDateTime deathDtm;

    @Column(name = "ZIP_CODE", length = 5)
    private String zipCode;

    @Column(name = "ADDRESS", length = 300)
    private String address;

    @Column(name = "ADDRESS_DETAIL", length = 300)
    private String addressDetail;

    @Column(name = "PHONE_NO", length = 20)
    private String phoneNo;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

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
