package kr.co.seoulit.his.patientservice.patient.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "PATIENT")

public class PatientEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "patientSequenceGenerator")
    @SequenceGenerator(
            name = "patientSequenceGenerator",
            sequenceName = "PATIENT_SEQ",
            allocationSize = 1)
    @Column(
            name = "PATIENT_ID",
            nullable = false)
    private Long patientId;

    @Column(name = "PATIENT_NAME")
    private String patientName;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "RESIDENT_REG_NO")
    private String residentRegNo;

    @Column(name = "STATUS_CD")
    private String statusCd;

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
