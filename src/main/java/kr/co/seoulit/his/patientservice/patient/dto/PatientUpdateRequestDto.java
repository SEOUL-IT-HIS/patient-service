package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequestDto(
        @NotBlank(message = "환자명은 필수입니다.")
        @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
        @Schema(description = "환자명. 입력 문자열 기준 2~100자 검증 후 trim, 이후 최소 길이 재검증 없음", example = "홍길동")
        String patientName) {
}
