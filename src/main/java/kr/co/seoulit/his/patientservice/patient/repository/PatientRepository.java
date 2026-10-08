package kr.co.seoulit.his.patientservice.patient.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {
    @Query(value = "SELECT TEMP_PATIENT_NO_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long nextTempPatientNo();

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PatientEntity p WHERE p.patientId = :patientId")
    java.util.Optional<PatientEntity> lockPatient(@Param("patientId") UUID patientId);

    @Query("""
            SELECT p FROM PatientEntity p
            WHERE (:patientName IS NULL OR p.patientName LIKE CONCAT('%', CONCAT(:patientName, '%')))
              AND (:birthDate IS NULL OR p.birthDate = :birthDate)
              AND (:statusCd IS NULL OR p.statusCd = :statusCd)
              AND p.mergedToPatientId IS NULL
            ORDER BY p.createdAt DESC, p.patientId DESC
            """)
    org.springframework.data.domain.Page<PatientEntity> searchPatientPage(
            @Param("patientName") String patientName,
            @Param("birthDate") LocalDate birthDate,
            @Param("statusCd") PatientStatus statusCd,
            org.springframework.data.domain.Pageable pageable);

    @Query(
            """
                    SELECT p
                    FROM PatientEntity p
                    WHERE (:patientName IS NULL
                           OR p.patientName LIKE CONCAT('%', CONCAT(:patientName, '%')))
                      AND (:birthDate IS NULL
                           OR p.birthDate = :birthDate)
                      AND (:statusCd IS NULL
                           OR p.statusCd = :statusCd)
                      AND p.mergedToPatientId IS NULL
                    ORDER BY p.createdAt DESC
                    """)
    List<PatientEntity> searchPatients(
            @Param("patientName") String patientName,
            @Param("birthDate") LocalDate birthDate,
            @Param("statusCd") PatientStatus statusCd);

    boolean existsByResidentRegNoAndMergedToPatientIdIsNull(String residentRegNo);

    boolean existsByResidentRegNoAndPatientIdNotAndMergedToPatientIdIsNull(
            String residentRegNo, UUID patientId);

    @Query("""
            SELECT p FROM PatientEntity p
            WHERE p.residentRegNo = :residentRegNo
              AND p.patientId <> :excludePatientId
              AND p.tempPatientYn = 'N'
              AND p.mergedToPatientId IS NULL
            ORDER BY p.createdAt ASC
            """)
    List<PatientEntity> findRegularMergeCandidates(
            @Param("residentRegNo") String residentRegNo,
            @Param("excludePatientId") UUID excludePatientId);

    boolean existsByPatientIdAndStatusCdAndDeathYn(
            UUID patientId,
            PatientStatus statusCd,
            String deathYn);
}
