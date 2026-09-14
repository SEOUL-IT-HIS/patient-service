package kr.co.seoulit.his.patientservice.patientcontact.entity;

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
@Table(name = "PATIENT_CONTACT")
public class PatientContactEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "CONTACT_ID",
            nullable = false,
            updatable = false,
            length = 36,
            columnDefinition = "VARCHAR2(36 CHAR)")
    private UUID contactId;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(
            name = "PATIENT_ID",
            nullable = false,
            updatable = false,
            length = 36,
            columnDefinition = "VARCHAR2(36 CHAR)")
    private UUID patientId;

    @Column(
            name = "ZIP_CODE",
            length = 5,
            columnDefinition = "VARCHAR2(5 CHAR)")
    private String zipCode;

    @Column(
            name = "ADDRESS",
            length = 300,
            columnDefinition = "VARCHAR2(300 CHAR)")
    private String address;

    @Column(
            name = "ADDRESS_DETAIL",
            length = 300,
            columnDefinition = "VARCHAR2(300 CHAR)")
    private String addressDetail;

    @Column(
            name = "PHONE_NO",
            length = 20,
            columnDefinition = "VARCHAR2(20 CHAR)")
    private String phoneNo;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(
            name = "PRIMARY_YN",
            nullable = false,
            length = 1,
            columnDefinition = "CHAR(1 BYTE)")
    private String primaryYn = "N";

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

    @Column(
            name = "UPDATED_AT",
            nullable = false)
    private LocalDateTime updatedAt;

    public static PatientContactEntity create(
            UUID patientId,
            String zipCode,
            String address,
            String addressDetail,
            String phoneNo,
            boolean primary) {

        PatientContactEntity entity = new PatientContactEntity();
        entity.patientId = patientId;
        entity.zipCode = zipCode;
        entity.address = address;
        entity.addressDetail = addressDetail;
        entity.phoneNo = phoneNo;
        entity.primaryYn = primary ? "Y" : "N";

        return entity;
    }

    public void update(
            String zipCode,
            String address,
            String addressDetail,
            String phoneNo) {

        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phoneNo = phoneNo;
    }

    public void setPrimary() {
        this.primaryYn = "Y";
    }

    public void clearPrimary() {
        this.primaryYn = "N";
    }

    public void deactivate() {
        this.activeYn = "N";
        this.primaryYn = "N";
    }

    public boolean isActive() {
        return "Y".equals(this.activeYn);
    }

    public boolean isPrimary() {
        return "Y".equals(this.primaryYn);
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