package kr.co.seoulit.his.patientservice.patientsafety.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientsafety.entity.PatientSafetyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientSafetyRepository
        extends JpaRepository<PatientSafetyEntity, UUID> {

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