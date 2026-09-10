package kr.co.seoulit.his.patientservice.patientsafety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PatientSafetyPinRequestDto(
        @NotNull @Schema(description = "true: 고정, false: 해제", example = "true") Boolean pinned) {
}
