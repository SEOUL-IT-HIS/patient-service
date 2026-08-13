package kr.co.seoulit.his.patientservice.patient.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDeathUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
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

    return PatientRegisterResponseDto.from(savedPatient);
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
        .map(PatientListResponseDto::from)
        .toList();
  }

  @Override
  public PatientDetailResponseDto updatePatientName(UUID patientId, PatientUpdateRequestDto dto) {
    PatientEntity patient =
        patientRepository
            .findById(patientId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

    patient.setPatientName(dto.patientName().trim());

    PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

    return PatientDetailResponseDto.from(updatedPatient);
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
    } else {
      patient.setDeathYn("N");
      patient.setDeathDtm(null);
    }

    PatientEntity updatedPatient = patientRepository.saveAndFlush(patient);

    return PatientDetailResponseDto.from(updatedPatient);
  }

  @Override
  public PatientDetailResponseDto deactivatePatient(UUID patientId) {
    PatientEntity patient =
        patientRepository
            .findById(patientId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

    if (patient.getStatusCd() == PatientStatus.INACTIVE) {
      return PatientDetailResponseDto.from(patient);
    }

    patient.setStatusCd(PatientStatus.INACTIVE);

    PatientEntity deactivatedPatient = patientRepository.saveAndFlush(patient);

    return PatientDetailResponseDto.from(deactivatedPatient);
  }

  @Override
  @Transactional(readOnly = true)
  public PatientValidationResponseDto validatePatient(UUID patientId) {
    boolean valid = patientRepository.existsByPatientIdAndStatusCd(patientId, PatientStatus.ACTIVE);

    return new PatientValidationResponseDto(patientId, valid);
  }

  @Override
  @Transactional(readOnly = true)
  public PatientDetailResponseDto getPatient(UUID patientId) {
    PatientEntity patient =
        patientRepository
            .findById(patientId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

    return PatientDetailResponseDto.from(patient);
  }
}
