package kr.co.seoulit.his.patientservice.patientsafety.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PatientSafetyPinRequestDto(
        @NotNull @Schema(description = "필수. true: 고정, false: 해제. 활성 정보만 가능하며 환자당 최대 2건 고정", example = "true", requiredMode = Schema.RequiredMode.REQUIRED) Boolean pinned) {
}
