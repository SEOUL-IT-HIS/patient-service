package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record PatientMergeRequestDto(
        @NotNull(message = "통합 대상 환자를 선택해야 합니다.")
        @Schema(description = "대표 환자로 유지할 기존 정규환자 UUID")
        UUID targetPatientId,

        @NotBlank(message = "주민등록번호는 필수입니다.")
        @Pattern(regexp = "\\d{13}", message = "주민등록번호는 숫자 13자리여야 합니다.")
        @Schema(description = "신원 확인에 사용한 주민등록번호, 하이픈 없는 13자리")
        String residentRegNo
) {
}
