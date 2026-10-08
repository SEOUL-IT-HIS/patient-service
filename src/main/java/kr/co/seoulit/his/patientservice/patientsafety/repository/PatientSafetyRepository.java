package kr.co.seoulit.his.patientservice.patientsafety.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientsafety.entity.PatientSafetyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PatientSafetyRepository
        extends JpaRepository<PatientSafetyEntity, UUID> {

    @Modifying
    @org.springframework.data.jpa.repository.Query(value = """
            UPDATE PATIENT_SAFETY_INFO
            SET PINNED_YN = 'N', UPDATED_AT = SYSTIMESTAMP
            WHERE PATIENT_ID = :sourcePatientId
              AND ACTIVE_YN = 'Y'
              AND PINNED_YN = 'Y'
              AND SAFETY_INFO_ID IN (
                  SELECT SAFETY_INFO_ID
                  FROM (
                      SELECT SAFETY_INFO_ID,
                             ROW_NUMBER() OVER (ORDER BY CREATED_AT DESC, SAFETY_INFO_ID DESC) AS ROW_NUM
                      FROM PATIENT_SAFETY_INFO
                      WHERE PATIENT_ID = :sourcePatientId
                        AND ACTIVE_YN = 'Y'
                        AND PINNED_YN = 'Y'
                  )
                  WHERE ROW_NUM > :keepPinnedCount
              )
            """, nativeQuery = true)
    int unpinOverflowingSourceSafetyInfo(
            @org.springframework.data.repository.query.Param("sourcePatientId") UUID sourcePatientId,
            @org.springframework.data.repository.query.Param("keepPinnedCount") int keepPinnedCount);

    @Modifying
    @org.springframework.data.jpa.repository.Query(value = """
            UPDATE PATIENT_SAFETY_INFO
            SET PATIENT_ID = :targetPatientId, UPDATED_AT = SYSTIMESTAMP
            WHERE PATIENT_ID = :sourcePatientId
            """, nativeQuery = true)
    int moveToPatient(
            @org.springframework.data.repository.query.Param("sourcePatientId") UUID sourcePatientId,
            @org.springframework.data.repository.query.Param("targetPatientId") UUID targetPatientId);

    long countByPatientIdAndActiveYnAndPinnedYn(UUID patientId, String activeYn, String pinnedYn);

    @org.springframework.data.jpa.repository.Query("""
            SELECT s FROM PatientSafetyEntity s
            WHERE s.patientId = :patientId AND (:includeInactive = true OR s.activeYn = 'Y')
            ORDER BY s.activeYn DESC, s.pinnedYn DESC, s.createdAt DESC, s.safetyInfoId DESC
            """)
    List<PatientSafetyEntity> findOrdered(
            @org.springframework.data.repository.query.Param("patientId") UUID patientId,
            @org.springframework.data.repository.query.Param("includeInactive") boolean includeInactive);

    List<PatientSafetyEntity>
    findByPatientIdAndActiveYnOrderByCreatedAtDescSafetyInfoIdDesc(
            UUID patientId,
            String activeYn);

    List<PatientSafetyEntity>
    findByPatientIdOrderByCreatedAtDescSafetyInfoIdDesc(
            UUID patientId);

    Optional<PatientSafetyEntity>
    findBySafetyInfoIdAndPatientId(
            UUID safetyInfoId,
            UUID patientId);
}
