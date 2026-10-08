package kr.co.seoulit.his.patientservice.patientcontact.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientcontact.entity.PatientContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientContactRepository
        extends JpaRepository<PatientContactEntity, UUID> {

    @Modifying
    @org.springframework.data.jpa.repository.Query(value = """
            UPDATE PATIENT_CONTACT
            SET PATIENT_ID = :targetPatientId,
                PRIMARY_YN = CASE
                    WHEN :targetHasPrimary = 'Y' AND ACTIVE_YN = 'Y' AND PRIMARY_YN = 'Y' THEN 'N'
                    ELSE PRIMARY_YN
                END,
                UPDATED_AT = SYSTIMESTAMP
            WHERE PATIENT_ID = :sourcePatientId
            """, nativeQuery = true)
    int moveToPatient(
            @org.springframework.data.repository.query.Param("sourcePatientId") UUID sourcePatientId,
            @org.springframework.data.repository.query.Param("targetPatientId") UUID targetPatientId,
            @org.springframework.data.repository.query.Param("targetHasPrimary") String targetHasPrimary);

    Optional<PatientContactEntity> findByContactIdAndPatientId(
            UUID contactId,
            UUID patientId);

    Optional<PatientContactEntity>
    findByPatientIdAndPrimaryYnAndActiveYn(
            UUID patientId,
            String primaryYn,
            String activeYn);

    long countByPatientIdAndActiveYn(
            UUID patientId,
            String activeYn);

    @Query("""
            SELECT c
            FROM PatientContactEntity c
            WHERE c.patientId = :patientId
              AND (:includeInactive = true OR c.activeYn = 'Y')
            ORDER BY c.activeYn DESC,
                     c.primaryYn DESC,
                     c.createdAt DESC,
                     c.contactId DESC
            """)
    List<PatientContactEntity> findOrdered(
            @Param("patientId") UUID patientId,
            @Param("includeInactive") boolean includeInactive);
}
