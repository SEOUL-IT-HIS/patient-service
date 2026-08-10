package kr.co.seoulit.his.patientservice.patient.dto;

import java.util.UUID;

public record PatientValidationResponseDto(
        UUID patientId,
        boolean valid
) {
}
