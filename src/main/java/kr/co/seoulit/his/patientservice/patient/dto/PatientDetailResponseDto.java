package kr.co.seoulit.his.patientservice.patient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

public record PatientDetailResponseDto(
    UUID patientId,
    String patientName,
    String residentRegNo,
    LocalDate birthDate,
    String genderCd,
    PatientStatus statusCd,
    String tempPatientYn,
    String deathYn,
    LocalDateTime deathDtm,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static PatientDetailResponseDto from(PatientEntity patient) {
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
