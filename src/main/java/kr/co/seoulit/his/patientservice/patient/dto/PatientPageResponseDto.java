package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PatientPageResponseDto(
        @Schema(description = "현재 페이지의 환자 목록, 최대 15명") List<PatientListResponseDto> items,
        @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1") int page,
        @Schema(description = "고정 페이지 크기", example = "15") int size,
        @Schema(description = "검색 조건에 해당하는 전체 환자 수") long totalElements,
        @Schema(description = "전체 페이지 수. 결과가 없으면 0") int totalPages) {
}
