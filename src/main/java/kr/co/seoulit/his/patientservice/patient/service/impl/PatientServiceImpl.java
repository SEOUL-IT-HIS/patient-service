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

        LocalDate birthDateFromResidentRegNo =
                ResidentRegNoUtils.extractBirthDate(dto.getResidentRegNo());

        if (!birthDateFromResidentRegNo.equals(dto.getBirthDate())) {
            throw new BusinessException(ErrorCode.BIRTH_DATE_MISMATCH);
        }

        if (patientRepository.existsByResidentRegNo(dto.getResidentRegNo())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESIDENT_REG_NO);
        }

        PatientEntity entity = PatientMapper.toEntity(dto);
        PatientEntity savedPatient = patientRepository.save(entity);

        return PatientMapper.toRegisterResponseDto(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentRegNoDuplicate(String residentRegNo) {
        ResidentRegNoUtils.extractBirthDate(residentRegNo);

        return patientRepository.existsByResidentRegNo(residentRegNo);
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
        PatientEntity patient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        patient.setPatientName(dto.patientName().trim());
        patient.setZipCode(normalize(dto.zipCode()));
        patient.setAddress(normalize(dto.address()));
        patient.setAddressDetail(normalize(dto.addressDetail()));
        patient.setPhoneNo(normalize(dto.phoneNo()));

        PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(updatedPatient);
    }

    @Override
    public PatientDetailResponseDto updateDeathStatus(
            UUID patientId, PatientDeathUpdateRequestDto dto) {
        PatientEntity patient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

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
        PatientEntity patient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        if (patient.getStatusCd() == PatientStatus.INACTIVE) {
            return PatientMapper.toDetailResponseDto(patient);
        }

        patient.setStatusCd(PatientStatus.INACTIVE);

        PatientEntity deactivatedPatient = patientRepository.saveAndFlush(patient);

        return PatientMapper.toDetailResponseDto(deactivatedPatient);
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
        PatientEntity patient =
                patientRepository
                        .findById(patientId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        return PatientMapper.toDetailResponseDto(patient);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

}
