package kr.co.seoulit.his.patientservice.patient.repository;


import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import java.util.List;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<PatientEntity, UUID> {

    List<PatientEntity> findByPatientNameOrderByPatientIdAsc(String patientName);

    boolean existsByResidentRegNo(String residentRegNo);

    boolean existsByPatientIdAndStatusCd(
            UUID patientId,
            PatientStatus statusCd
    );
}
