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

  List<PatientEntity> findByPatientNameOrderByPatientIdAsc(String patientName);

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
      ORDER BY p.createdAt DESC
      """)
  List<PatientEntity> searchPatients(
      @Param("patientName") String patientName,
      @Param("birthDate") LocalDate birthDate,
      @Param("statusCd") PatientStatus statusCd);

  boolean existsByResidentRegNo(String residentRegNo);

  boolean existsByPatientIdAndStatusCdAndDeathYn(
          UUID patientId,
          PatientStatus statusCd,
          String deathYn
  );
}
