package kr.co.seoulit.his.patientservice.patientsafety.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientsafety.entity.PatientSafetyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientSafetyRepository
        extends JpaRepository<PatientSafetyEntity, UUID> {

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
