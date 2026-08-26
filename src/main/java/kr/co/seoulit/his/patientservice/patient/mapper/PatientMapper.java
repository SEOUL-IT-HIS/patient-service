package kr.co.seoulit.his.patientservice.patient.mapper;

import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import org.springframework.stereotype.Component;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import kr.co.seoulit.his.patientservice.patient.dto.PatientBatchResponseDto;

/** [Mapper] DTO ↔ Entity 변환 - toEntity: 등록 시 DTO → Entity */
@Component
public class PatientMapper {

  public static PatientEntity toEntity(PatientDto dto) {
    PatientEntity entity = new PatientEntity();
    entity.setPatientName(dto.getPatientName());
    entity.setBirthDate(dto.getBirthDate());
    entity.setResidentRegNo(dto.getResidentRegNo());
    entity.setGenderCd(dto.getGenderCd());
    entity.setStatusCd(PatientStatus.ACTIVE);
    entity.setTempPatientYn(dto.getTempPatientYn());
    return entity;
  }

  public static PatientRegisterResponseDto toRegisterResponseDto(PatientEntity patient) {
    return new PatientRegisterResponseDto(
        patient.getPatientId(),
        patient.getPatientName(),
        patient.getBirthDate(),
        patient.getGenderCd(),
        patient.getStatusCd(),
        patient.getTempPatientYn(),
        patient.getCreatedAt());
  }

  public static PatientListResponseDto toListResponseDto(PatientEntity patient) {
    return new PatientListResponseDto(
        patient.getPatientId(),
        patient.getPatientName(),
        maskResidentRegNo(patient.getResidentRegNo()),
        patient.getBirthDate(),
        patient.getGenderCd(),
        patient.getStatusCd(),
        patient.getTempPatientYn(),
        patient.getDeathYn(),
        patient.getCreatedAt(),
        patient.getUpdatedAt());
  }

  public static PatientBatchResponseDto toBatchResponseDto(PatientEntity patient) {
    return new PatientBatchResponseDto(
            patient.getPatientId(),
            patient.getPatientName(),
            patient.getBirthDate(),
            patient.getGenderCd(),
            patient.getStatusCd());
  }

  public static PatientDetailResponseDto toDetailResponseDto(PatientEntity patient) {
    return new PatientDetailResponseDto(
        patient.getPatientId(),
        patient.getPatientName(),
        maskResidentRegNo(patient.getResidentRegNo()),
        patient.getBirthDate(),
        patient.getGenderCd(),
        patient.getStatusCd(),
        patient.getTempPatientYn(),
        patient.getDeathYn(),
        patient.getDeathDtm(),
        patient.getCreatedAt(),
        patient.getUpdatedAt());
  }

  private static String maskResidentRegNo(String residentRegNo) {
    if (residentRegNo == null || residentRegNo.isBlank()) {
      return "";
    }

    String digits = residentRegNo.replaceAll("[^0-9]", "");
    if (digits.length() < 7) {
      return residentRegNo;
    }

    return digits.substring(0, 6) + "-" + digits.charAt(6) + "******";
  }
}
