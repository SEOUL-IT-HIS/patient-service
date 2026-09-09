package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

public record PatientBatchResponseDto(
        @Schema(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,
        @Schema(description = "환자명", example = "홍길동")
        String patientName,
        @Schema(description = "생년월일", example = "1990-01-01")
        LocalDate birthDate,
        @Schema(description = "성별 공통코드 (01, 02, 03, 04)", example = "01")
        String genderCd,
        @Schema(description = "환자 상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
        PatientStatus statusCd
) {
}