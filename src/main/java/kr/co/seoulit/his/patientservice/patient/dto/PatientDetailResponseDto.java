package kr.co.seoulit.his.patientservice.patient.dto;

import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientDetailResponseDto(
        Long patientId,
        String patientName,
        String residentRegNo,
        LocalDate birthDate,
        PatientStatus statusCd,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PatientDetailResponseDto from(PatientEntity patient) {
        return new PatientDetailResponseDto(
                patient.getPatientId(),
                patient.getPatientName(),
                maskResidentRegNo(patient.getResidentRegNo()),
                patient.getBirthDate(),
                patient.getStatusCd(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    private static String maskResidentRegNo(String residentRegNo) {
        if (residentRegNo == null || residentRegNo.isBlank()) {
            return "";
        }

        String digits = residentRegNo.replaceAll("[^0-9]", "");

        if (digits.length() < 7) {
            return residentRegNo;
        }

        return digits.substring(0, 6)
                + "-"
                + digits.charAt(6)
                + "******";
    }
}