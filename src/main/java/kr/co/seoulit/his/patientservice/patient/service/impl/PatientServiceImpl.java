package kr.co.seoulit.his.patientservice.patient.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDeathUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientBatchResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientTemporaryConversionRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientMergeRequestDto;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactCreateRequestDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.mapper.PatientMapper;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
import kr.co.seoulit.his.patientservice.patientcontact.repository.PatientContactRepository;
import kr.co.seoulit.his.patientservice.patientcontact.service.PatientContactService;
import kr.co.seoulit.his.patientservice.patientsafety.repository.PatientSafetyRepository;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import kr.co.seoulit.his.patientservice.patient.util.ResidentRegNoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientContactRepository patientContactRepository;
    private final PatientContactService patientContactService;
    private final PatientSafetyRepository patientSafetyRepository;

    @Override
    @Transactional(readOnly = true)
    public kr.co.seoulit.his.patientservice.patient.dto.PatientPageResponseDto getPatientPage(
            String patientName, LocalDate birthDate, PatientStatus statusCd, int page) {
        if (page < 1) throw new BusinessException(ErrorCode.INVALID_INPUT);
        String name = patientName == null || patientName.isBlank() ? null : patientName.trim();
        var result = patientRepository.searchPatientPage(name, birthDate, statusCd,
                org.springframework.data.domain.PageRequest.of(page - 1, 15));
        if (page > Math.max(1, result.getTotalPages())) {
            result = patientRepository.searchPatientPage(name, birthDate, statusCd,
                    org.springframework.data.domain.PageRequest.of(Math.max(0, result.getTotalPages() - 1), 15));
        }
        return new kr.co.seoulit.his.patientservice.patient.dto.PatientPageResponseDto(
                result.getContent().stream().map(PatientMapper::toListResponseDto).toList(),
                result.getNumber() + 1, 15, result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public PatientRegisterResponseDto createPatient(PatientDto dto) {

        boolean temporaryPatient = "Y".equals(dto.getTempPatientYn());

        if (temporaryPatient) {
            prepareTemporaryPatient(dto);
        } else {
            validateRegularPatient(dto);
        }

        PatientEntity entity = PatientMapper.toEntity(dto);
        if (temporaryPatient && entity.getPatientName() == null) {
            entity.setTempPatientNo(patientRepository.nextTempPatientNo());
        }
        PatientEntity savedPatient = patientRepository.save(entity);

        if (hasContactInformation(dto)) {
            patientContactService.createContact(
                    savedPatient.getPatientId(),
                    new PatientContactCreateRequestDto(
                            dto.getZipCode(),
                            dto.getAddress(),
                            dto.getAddressDetail(),
                            dto.getPhoneNo()));
        }

        return PatientMapper.toRegisterResponseDto(savedPatient);
    }

    private boolean hasContactInformation(PatientDto dto) {
        return hasText(dto.getZipCode())
                || hasText(dto.getAddress())
                || hasText(dto.getAddressDetail())
                || hasText(dto.getPhoneNo());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private void validateRegularPatient(PatientDto dto) {

        if (dto.getPatientName() == null || dto.getPatientName().isBlank()) {
            throw new BusinessException(ErrorCode.PATIENT_NAME_REQUIRED);
        }

        if (dto.getBirthDate() == null) {
            throw new BusinessException(ErrorCode.BIRTH_DATE_REQUIRED);
        }

        if (dto.getResidentRegNo() == null || dto.getResidentRegNo().isBlank()) {
            throw new BusinessException(ErrorCode.RESIDENT_REG_NO_REQUIRED);
        }

        LocalDate birthDateFromResidentRegNo =
                ResidentRegNoUtils.extractBirthDate(dto.getResidentRegNo());

        if (!birthDateFromResidentRegNo.equals(dto.getBirthDate())) {
            throw new BusinessException(ErrorCode.BIRTH_DATE_MISMATCH);
        }

        if (patientRepository.existsByResidentRegNoAndMergedToPatientIdIsNull(dto.getResidentRegNo())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESIDENT_REG_NO);
        }

        dto.setPatientName(dto.getPatientName().trim());
        dto.setTempRegisterReason(null);
    }

    private void prepareTemporaryPatient(PatientDto dto) {

        if (dto.getTempRegisterReason() == null
                || dto.getTempRegisterReason().isBlank()) {
            throw new BusinessException(ErrorCode.TEMP_REGISTER_REASON_REQUIRED);
        }

        dto.setTempRegisterReason(dto.getTempRegisterReason().trim());

        if (dto.getPatientName() == null || dto.getPatientName().isBlank()) {
            dto.setPatientName(null);
        } else {
            dto.setPatientName(dto.getPatientName().trim());
        }

        if (dto.getResidentRegNo() == null || dto.getResidentRegNo().isBlank()) {
            dto.setResidentRegNo(null);
            return;
        }

        LocalDate birthDateFromResidentRegNo =
                ResidentRegNoUtils.extractBirthDate(dto.getResidentRegNo());

        if (dto.getBirthDate() == null) {
            dto.setBirthDate(birthDateFromResidentRegNo);
        } else if (!birthDateFromResidentRegNo.equals(dto.getBirthDate())) {
            throw new BusinessException(ErrorCode.BIRTH_DATE_MISMATCH);
        }

        if (patientRepository.existsByResidentRegNoAndMergedToPatientIdIsNull(dto.getResidentRegNo())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESIDENT_REG_NO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentRegNoDuplicate(String residentRegNo, UUID excludePatientId) {
        ResidentRegNoUtils.extractBirthDate(residentRegNo);

        return excludePatientId == null
                ? patientRepository.existsByResidentRegNoAndMergedToPatientIdIsNull(residentRegNo)
                : patientRepository.existsByResidentRegNoAndPatientIdNotAndMergedToPatientIdIsNull(
                        residentRegNo,
                        excludePatientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientListResponseDto> findTemporaryConversionCandidates(
            String residentRegNo, UUID excludePatientId) {
        ResidentRegNoUtils.extractBirthDate(residentRegNo);
        PatientEntity source = getUnmergedPatientOrThrow(excludePatientId);
        if (!"Y".equals(source.getTempPatientYn())) {
            throw new BusinessException(ErrorCode.NOT_TEMPORARY_PATIENT);
        }
        return patientRepository.findRegularMergeCandidates(residentRegNo, excludePatientId)
                .stream()
                .map(PatientMapper::toListResponseDto)
                .toList();
    }

    @Override
    public PatientDetailResponseDto mergeTemporaryPatient(
            UUID temporaryPatientId, PatientMergeRequestDto request) {
        PatientEntity source = patientRepository.lockPatient(temporaryPatientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));
        PatientEntity target = patientRepository.lockPatient(request.targetPatientId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        if (source.getMergedToPatientId() != null) {
            throw new BusinessException(ErrorCode.PATIENT_ALREADY_MERGED);
        }
        if (!"Y".equals(source.getTempPatientYn())) {
            throw new BusinessException(ErrorCode.NOT_TEMPORARY_PATIENT);
        }
        if (target.getMergedToPatientId() != null || "Y".equals(target.getTempPatientYn())) {
            throw new BusinessException(ErrorCode.INVALID_MERGE_TARGET);
        }

        LocalDate residentBirthDate = ResidentRegNoUtils.extractBirthDate(request.residentRegNo());
        if (!request.residentRegNo().equals(target.getResidentRegNo())
                || !residentBirthDate.equals(target.getBirthDate())) {
            throw new BusinessException(ErrorCode.PATIENT_IDENTITY_MISMATCH);
        }

        boolean targetHasPrimaryContact = patientContactRepository
                .findByPatientIdAndPrimaryYnAndActiveYn(target.getPatientId(), "Y", "Y")
                .isPresent();
        patientContactRepository.moveToPatient(
                source.getPatientId(), target.getPatientId(), targetHasPrimaryContact ? "Y" : "N");

        long targetPinnedCount = patientSafetyRepository
                .countByPatientIdAndActiveYnAndPinnedYn(target.getPatientId(), "Y", "Y");
        int remainingPinnedSlots = (int) Math.max(0, 2 - targetPinnedCount);
        patientSafetyRepository.unpinOverflowingSourceSafetyInfo(
                source.getPatientId(), remainingPinnedSlots);
        patientSafetyRepository.moveToPatient(source.getPatientId(), target.getPatientId());

        source.setMergedToPatientId(target.getPatientId());
        source.setMergedAt(LocalDateTime.now());
        patientRepository.saveAndFlush(source);
        return PatientMapper.toDetailResponseDto(target);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientListResponseDto> getPatients(
            String patientName, LocalDate birthDate, PatientStatus statusCd) {
        String normalizedPatientName =
                patientName == null || patientName.isBlank() ? null : patientName.trim();

        return patientRepository.searchPatients(normalizedPatientName, birthDate, statusCd).stream()
                .map(PatientMapper::toListResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientBatchResponseDto> getPatientsByIds(List<UUID> patientIds) {
        List<UUID> distinctIds =
                new ArrayList<>(new LinkedHashSet<>(patientIds));

        Map<UUID, PatientEntity> patientsById =
                patientRepository.findAllById(distinctIds).stream()
                        .collect(
                                Collectors.toMap(
                                        PatientEntity::getPatientId,
                                        Function.identity()));

        LinkedHashSet<UUID> canonicalIds = new LinkedHashSet<>();
        for (UUID id : distinctIds) {
            PatientEntity patient = patientsById.get(id);
            if (patient == null) continue;
            canonicalIds.add(patient.getMergedToPatientId() == null
                    ? patient.getPatientId() : patient.getMergedToPatientId());
        }
        Map<UUID, PatientEntity> canonicalPatients = patientRepository.findAllById(canonicalIds)
                .stream()
                .collect(Collectors.toMap(PatientEntity::getPatientId, Function.identity()));
        return canonicalIds.stream()
                .map(canonicalPatients::get)
                .filter(patient -> patient != null)
                .map(PatientMapper::toBatchResponseDto)
                .toList();
    }

    @Override
    public PatientDetailResponseDto updatePatientInfo(UUID patientId, PatientUpdateRequestDto dto) {
        PatientEntity patient = getUnmergedPatientOrThrow(patientId);

        patient.setPatientName(dto.patientName().trim());

        PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(updatedPatient);
    }

    @Override
    public PatientDetailResponseDto convertTemporaryPatient(
            UUID patientId,
            PatientTemporaryConversionRequestDto dto
    ) {
        PatientEntity patient = getUnmergedPatientOrThrow(patientId);

        if (!"Y".equals(patient.getTempPatientYn())) {
            throw new BusinessException(
                    ErrorCode.NOT_TEMPORARY_PATIENT
            );
        }

        String normalizedPatientName = dto.patientName().trim();

        if (normalizedPatientName.length() < 2 || normalizedPatientName.length() > 100) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT,
                    "환자명은 2자 이상 100자 이하여야 합니다.");
        }

        LocalDate birthDateFromResidentRegNo =
                ResidentRegNoUtils.extractBirthDate(dto.residentRegNo());

        if (!birthDateFromResidentRegNo.equals(dto.birthDate())) {
            throw new BusinessException(ErrorCode.BIRTH_DATE_MISMATCH);
        }

        boolean duplicated = patientRepository.existsByResidentRegNoAndPatientIdNotAndMergedToPatientIdIsNull(
                dto.residentRegNo(), patientId);

        if (duplicated) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESIDENT_REG_NO);
        }

        patient.setPatientName(normalizedPatientName);
        patient.setResidentRegNo(dto.residentRegNo());
        patient.setBirthDate(dto.birthDate());
        patient.setGenderCd(dto.genderCd());
        patient.setTempPatientYn("N");

        PatientEntity convertedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(convertedPatient);
    }

    @Override
    public PatientDetailResponseDto updateDeathStatus(
            UUID patientId, PatientDeathUpdateRequestDto dto) {
        PatientEntity patient = getUnmergedPatientOrThrow(patientId);

        if ("Y".equals(dto.deathYn())) {
            if (dto.deathDtm() == null) {
                throw new BusinessException(ErrorCode.DEATH_DATE_REQUIRED);
            }

            if (dto.deathDtm().isAfter(LocalDateTime.now())) {
                throw new BusinessException(ErrorCode.INVALID_DEATH_DATE);
            }

            patient.setDeathYn("Y");
            patient.setDeathDtm(dto.deathDtm());
            patient.setStatusCd(PatientStatus.INACTIVE);
        } else {
            patient.setDeathYn("N");
            patient.setDeathDtm(null);
        }

        PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(updatedPatient);
    }

    @Override
    public PatientDetailResponseDto deactivatePatient(UUID patientId) {
        PatientEntity patient = getUnmergedPatientOrThrow(patientId);

        if (patient.getStatusCd() == PatientStatus.INACTIVE) {
            return PatientMapper.toDetailResponseDto(patient);
        }

        patient.setStatusCd(PatientStatus.INACTIVE);

        PatientEntity deactivatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(deactivatedPatient);
    }

    @Override
    public PatientDetailResponseDto activatePatient(UUID patientId) {
        PatientEntity patient = getUnmergedPatientOrThrow(patientId);

        if ("Y".equals(patient.getDeathYn())) {
            throw new BusinessException(ErrorCode.DECEASED_PATIENT_CANNOT_BE_ACTIVATED);
        }

        if (patient.getStatusCd() == PatientStatus.ACTIVE) {
            return PatientMapper.toDetailResponseDto(patient);
        }

        patient.setStatusCd(PatientStatus.ACTIVE);

        PatientEntity activatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(activatedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientValidationResponseDto validatePatient(UUID patientId) {
        PatientEntity patient = patientRepository.findById(patientId).orElse(null);
        boolean valid = patient != null
                && patient.getMergedToPatientId() == null
                && patientRepository.existsByPatientIdAndStatusCdAndDeathYn(
                        patientId,
                        PatientStatus.ACTIVE,
                        "N"
                );

        return new PatientValidationResponseDto(patientId, valid);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDetailResponseDto getPatient(UUID patientId) {
        PatientEntity patient = getPatientOrThrow(patientId);

        return PatientMapper.toDetailResponseDto(patient);
    }

    private PatientEntity getPatientOrThrow(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));
    }

    private PatientEntity getUnmergedPatientOrThrow(UUID patientId) {
        PatientEntity patient = getPatientOrThrow(patientId);
        if (patient.getMergedToPatientId() != null) {
            throw new BusinessException(ErrorCode.PATIENT_ALREADY_MERGED);
        }
        return patient;
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
