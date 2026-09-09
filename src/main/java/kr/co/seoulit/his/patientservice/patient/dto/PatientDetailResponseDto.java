package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

public record PatientDetailResponseDto(
        @Schema(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID patientId,
        @Schema(description = "환자명", example = "홍길동")
        String patientName,
        @Schema(description = "마스킹된 주민등록번호", example = "900101-1******")
        String residentRegNo,
        @Schema(description = "생년월일", example = "1990-01-01")
        LocalDate birthDate,
        @Schema(description = "성별 공통코드 (01, 02, 03, 04)", example = "01")
        String genderCd,
        @Schema(description = "환자 상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
        PatientStatus statusCd,
        @Schema(description = "임시환자 여부 (Y/N)", example = "N")
        String tempPatientYn,
        @Schema(description = "임시환자 등록 시 필수 사유", example = "신원 확인 중")
        String tempRegisterReason,
        @Schema(description = "사망 여부 (Y/N)", example = "N")
        String deathYn,
        @Schema(description = "사망일시. 사망 처리 시 필수이며 미래 시각 불가")
        LocalDateTime deathDtm,
        @Schema(description = "우편번호, 숫자 5자리", example = "06236")
        String zipCode,
        @Schema(description = "기본주소, 최대 300자", example = "서울특별시 강남구 테헤란로 123")
        String address,
        @Schema(description = "상세주소, 최대 300자", example = "401호")
        String addressDetail,
        @Schema(description = "연락처, 숫자 9~11자리", example = "01012345678")
        String phoneNo,
        @Schema(description = "생성시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
        LocalDateTime createdAt,
        @Schema(description = "최종 수정시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
        LocalDateTime updatedAt) {
}
