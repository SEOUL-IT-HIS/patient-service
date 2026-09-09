package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record PatientDeathUpdateRequestDto(
        @NotBlank(message = "사망 여부는 필수입니다.")
        @Pattern(regexp = "^[YN]$", message = "사망 여부는 Y 또는 N이어야 합니다.")
        @Schema(description = "사망 여부 (Y/N)", example = "N")
        String deathYn,
        @Schema(description = "사망일시. 사망 처리 시 필수이며 미래 시각 불가")
        LocalDateTime deathDtm) {
}
