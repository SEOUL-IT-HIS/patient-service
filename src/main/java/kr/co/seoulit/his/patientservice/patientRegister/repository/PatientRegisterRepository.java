package kr.co.seoulit.his.patientservice.patientRegister.repository;


import kr.co.seoulit.his.patientservice.patientRegister.entity.PatientRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRegisterRepository extends JpaRepository<PatientRegisterEntity, Long> {

    List<PatientRegisterEntity> findByPatientNameOrderByPatientIdAsc(String patientName);

    boolean existsByResidentRegNo(String residentRegNo);
}
