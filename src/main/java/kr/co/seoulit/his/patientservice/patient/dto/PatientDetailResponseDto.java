package kr.co.seoulit.his.patientservice.patient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

public record PatientDetailResponseDto(
        UUID patientId,
        String patientName,
        String residentRegNo,
        LocalDate birthDate,
        String genderCd,
        PatientStatus statusCd,
        String tempPatientYn,
        String tempRegisterReason,
        String deathYn,
        LocalDateTime deathDtm,
        String zipCode,
        String address,
        String addressDetail,
        String phoneNo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
