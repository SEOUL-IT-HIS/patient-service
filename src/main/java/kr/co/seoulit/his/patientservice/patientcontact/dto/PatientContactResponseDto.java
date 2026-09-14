package kr.co.seoulit.his.patientservice.patientcontact.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record PatientContactResponseDto(

        @Schema(
                description = "연락처 UUID",
                example = "660e8400-e29b-41d4-a716-446655440001")
        UUID contactId,

        @Schema(
                description = "환자 UUID",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,

        @Schema(
                description = "우편번호",
                example = "06236")
        String zipCode,

        @Schema(
                description = "기본주소",
                example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Schema(
                description = "상세주소",
                example = "401호")
        String addressDetail,

        @Schema(
                description = "연락처",
                example = "01012345678")
        String phoneNo,

        @Schema(
                description = "대표 주소·연락처 여부",
                example = "Y",
                allowableValues = {"Y", "N"})
        String primaryYn,

        @Schema(
                description = "활성 여부",
                example = "Y",
                allowableValues = {"Y", "N"})
        String activeYn,

        @Schema(
                description = "생성 시각",
                example = "2026-09-14T10:00:00")
        LocalDateTime createdAt,

        @Schema(
                description = "마지막 수정 시각",
                example = "2026-09-14T10:00:00")
        LocalDateTime updatedAt
) {
}