package kr.co.seoulit.his.patientservice.patientsafety.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record PatientSafetyResponseDto(

        @Schema(description = "안전정보 UUID", example = "660e8400-e29b-41d4-a716-446655440001")
        UUID safetyInfoId,
        @Schema(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,
        @Schema(description = "안전정보 내용. 공백 불가, UTF-8 기준 최대 2000바이트 (글자 수 제한이 아님)", example = "라텍스 알레르기 있음")
        String safetyNote,
        @Schema(description = "안전정보 활성 여부 (Y/N)", example = "Y")
        String activeYn,
        @Schema(description = "생성시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
        LocalDateTime createdAt,
        @Schema(description = "최종 수정시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
        LocalDateTime updatedAt

) {
}