package kr.co.seoulit.his.patientservice.patient.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.mapper.PatientMapper;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
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

    @Override
    public PatientRegisterResponseDto createPatient(PatientDto dto) {

        boolean temporaryPatient = "Y".equals(dto.getTempPatientYn());

        if (temporaryPatient) {
            prepareTemporaryPatient(dto);
        } else {
            validateRegularPatient(dto);
        }

        PatientEntity entity = PatientMapper.toEntity(dto);
        PatientEntity savedPatient = patientRepository.save(entity);

        return PatientMapper.toRegisterResponseDto(savedPatient);
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

        if (patientRepository.existsByResidentRegNo(dto.getResidentRegNo())) {
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
            dto.setPatientName(generateTemporaryPatientName());
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

        if (patientRepository.existsByResidentRegNo(dto.getResidentRegNo())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESIDENT_REG_NO);
        }
    }

    private String generateTemporaryPatientName() {

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        return "무명환자-" + suffix;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentRegNoDuplicate(String residentRegNo, UUID excludePatientId) {
        ResidentRegNoUtils.extractBirthDate(residentRegNo);

        return excludePatientId == null
                ? patientRepository.existsByResidentRegNo(residentRegNo)
                : patientRepository.existsByResidentRegNoAndPatientIdNot(
                        residentRegNo,
                        excludePatientId);
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

        return distinctIds.stream()
                .map(patientsById::get)
                .filter(Objects::nonNull)
                .map(PatientMapper::toBatchResponseDto)
                .toList();
    }

    @Override
    public PatientDetailResponseDto updatePatientInfo(UUID patientId, PatientUpdateRequestDto dto) {
        PatientEntity patient = getPatientOrThrow(patientId);

        patient.setPatientName(dto.patientName().trim());
        patient.setZipCode(normalize(dto.zipCode()));
        patient.setAddress(normalize(dto.address()));
        patient.setAddressDetail(normalize(dto.addressDetail()));
        patient.setPhoneNo(normalize(dto.phoneNo()));

        PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(updatedPatient);
    }

    @Override
    public PatientDetailResponseDto convertTemporaryPatient(
            UUID patientId,
            PatientTemporaryConversionRequestDto dto
    ) {
        PatientEntity patient = getPatientOrThrow(patientId);

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

        boolean duplicated = patientRepository.existsByResidentRegNoAndPatientIdNot(
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
        PatientEntity patient = getPatientOrThrow(patientId);

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
        PatientEntity patient = getPatientOrThrow(patientId);

        if (patient.getStatusCd() == PatientStatus.INACTIVE) {
            return PatientMapper.toDetailResponseDto(patient);
        }

        patient.setStatusCd(PatientStatus.INACTIVE);

        PatientEntity deactivatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(deactivatedPatient);
    }

    @Override
    public PatientDetailResponseDto activatePatient(UUID patientId) {
        PatientEntity patient = getPatientOrThrow(patientId);

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
        boolean valid =
                patientRepository.existsByPatientIdAndStatusCdAndDeathYn(
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

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
