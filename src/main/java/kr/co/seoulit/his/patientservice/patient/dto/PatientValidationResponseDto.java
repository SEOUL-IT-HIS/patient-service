package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record PatientValidationResponseDto(
        @Schema(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,
        @Schema(description = "환자가 존재하고 ACTIVE이며 사망 여부 N이면 true", example = "true")
        boolean valid
) {
}
