package kr.co.seoulit.his.patientservice.patient.dto;

import java.time.LocalDate;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

public record PatientBatchResponseDto(
        UUID patientId,
        String patientName,
        LocalDate birthDate,
        String genderCd,
        PatientStatus statusCd
) {
}