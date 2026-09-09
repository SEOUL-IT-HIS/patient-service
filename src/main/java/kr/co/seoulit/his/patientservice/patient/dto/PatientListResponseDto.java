package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatientListResponseDto {

    @Schema(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID patientId;
    @Schema(description = "환자명", example = "홍길동")
    private final String patientName;
    @Schema(description = "마스킹된 주민등록번호", example = "900101-1******")
    private final String residentRegNo;
    @Schema(description = "생년월일", example = "1990-01-01")
    private final LocalDate birthDate;
    @Schema(description = "성별 공통코드 (01, 02, 03, 04)", example = "01")
    private final String genderCd;
    @Schema(description = "환자 상태 (ACTIVE, INACTIVE)", example = "ACTIVE")
    private final PatientStatus statusCd;
    @Schema(description = "임시환자 여부 (Y/N)", example = "N")
    private final String tempPatientYn;
    @Schema(description = "사망 여부 (Y/N)", example = "N")
    private final String deathYn;
    @Schema(description = "생성시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
    private final LocalDateTime createdAt;
    @Schema(description = "최종 수정시각 (시간대 없는 서버 현지 시각)", example = "2026-09-09T10:00:00")
    private final LocalDateTime updatedAt;
}
