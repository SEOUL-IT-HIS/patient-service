package kr.co.seoulit.his.patientservice.patient.repository;


import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import java.util.List;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {

    List<PatientEntity> findByPatientNameOrderByPatientIdAsc(String patientName);

    boolean existsByResidentRegNo(String residentRegNo);

    boolean existsByPatientIdAndStatusCd(
            Long patientId,
            PatientStatus statusCd
    );
}
