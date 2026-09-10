package kr.co.seoulit.his.patientservice.patientsafety.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyCreateRequestDto;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyResponseDto;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patientsafety.entity.PatientSafetyEntity;
import kr.co.seoulit.his.patientservice.patientsafety.mapper.PatientSafetyMapper;
import kr.co.seoulit.his.patientservice.patientsafety.repository.PatientSafetyRepository;
import kr.co.seoulit.his.patientservice.patientsafety.service.PatientSafetyService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientSafetyServiceImpl implements PatientSafetyService {

    private final PatientRepository patientRepository;
    private final PatientSafetyRepository patientSafetyRepository;
    private final PatientSafetyMapper patientSafetyMapper;

    @Override
    @Transactional
    public PatientSafetyResponseDto createSafetyInfo(
            UUID patientId,
            PatientSafetyCreateRequestDto request) {

        validatePatientExists(patientId);
        validateSafetyNote(request.safetyNote());

        PatientSafetyEntity entity = PatientSafetyEntity.create(
                patientId,
                request.safetyNote()
        );

        PatientSafetyEntity saved =
                patientSafetyRepository.saveAndFlush(entity);

        return patientSafetyMapper.toResponseDto(saved);
    }

    @Override
    public List<PatientSafetyResponseDto> getSafetyInfoList(
            UUID patientId,
            boolean includeInactive) {

        validatePatientExists(patientId);

        List<PatientSafetyEntity> entities = patientSafetyRepository.findOrdered(patientId, includeInactive);

        return entities.stream()
                .map(patientSafetyMapper::toResponseDto)
                .toList();
    }

    @Override
    public PatientSafetyResponseDto getSafetyInfo(
            UUID patientId,
            UUID safetyInfoId) {

        PatientSafetyEntity entity =
                findSafetyInfo(patientId, safetyInfoId);

        return patientSafetyMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public PatientSafetyResponseDto updateSafetyInfo(
            UUID patientId,
            UUID safetyInfoId,
            PatientSafetyUpdateRequestDto request) {

        lockPatient(patientId);

        PatientSafetyEntity entity =
                findSafetyInfo(patientId, safetyInfoId);

        if (!entity.isActive()) {
            throw new BusinessException(ErrorCode.SAFETY_INFO_INACTIVE);
        }

        validateSafetyNote(request.safetyNote());

        entity.updateSafetyNote(request.safetyNote());

        // @PreUpdate로 변경된 수정 시각을 응답에 반영합니다.
        patientSafetyRepository.flush();

        return patientSafetyMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public PatientSafetyResponseDto deactivateSafetyInfo(
            UUID patientId,
            UUID safetyInfoId) {

        lockPatient(patientId);

        PatientSafetyEntity entity =
                findSafetyInfo(patientId, safetyInfoId);

        // 이미 비활성이면 변경 없이 반환합니다.
        if (entity.isActive()) {
            entity.deactivate();
            patientSafetyRepository.flush();
        }

        return patientSafetyMapper.toResponseDto(entity);
    }

    private void validatePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private void lockPatient(UUID patientId) {
        patientRepository.lockPatient(patientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));
    }

    @Override
    @Transactional
    public PatientSafetyResponseDto setPinned(UUID patientId, UUID safetyInfoId, boolean pinned) {
        // Serialize pin/unpin/deactivation for one patient, including concurrent requests.
        lockPatient(patientId);
        PatientSafetyEntity entity = findSafetyInfo(patientId, safetyInfoId);
        if (!entity.isActive()) throw new BusinessException(ErrorCode.SAFETY_INFO_INACTIVE);
        if (pinned && !"Y".equals(entity.getPinnedYn()) &&
                patientSafetyRepository.countByPatientIdAndActiveYnAndPinnedYn(patientId, "Y", "Y") >= 2) {
            throw new BusinessException(ErrorCode.SAFETY_PIN_LIMIT);
        }
        entity.setPinned(pinned);
        patientSafetyRepository.flush();
        return patientSafetyMapper.toResponseDto(entity);
    }

    private PatientSafetyEntity findSafetyInfo(
            UUID patientId,
            UUID safetyInfoId) {

        validatePatientExists(patientId);

        return patientSafetyRepository
                .findBySafetyInfoIdAndPatientId(safetyInfoId, patientId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.SAFETY_INFO_NOT_FOUND));
    }

    private void validateSafetyNote(String safetyNote) {
        if (safetyNote == null || safetyNote.isBlank()) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "환자 안전정보 내용은 필수입니다."
            );
        }

        int byteLength =
                safetyNote.getBytes(StandardCharsets.UTF_8).length;

        if (byteLength > 2000) {
            throw new BusinessException(ErrorCode.SAFETY_NOTE_TOO_LONG);
        }
    }

}
