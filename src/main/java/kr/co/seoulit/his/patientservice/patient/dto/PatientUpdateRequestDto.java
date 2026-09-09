package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequestDto(
        @NotBlank(message = "환자명은 필수입니다.")
        @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
        @Schema(description = "환자명", example = "홍길동")
        String patientName,

        @Pattern(
                regexp = "^$|^\\d{5}$",
                message = "우편번호는 숫자 5자리여야 합니다.")
        @Schema(description = "우편번호, 숫자 5자리; 생략/null/빈 문자열이면 기존 값 삭제", example = "06236")
        String zipCode,

        @Size(max = 300, message = "주소는 300자 이하여야 합니다.")
        @Schema(description = "기본주소, 최대 300자; 생략/null/빈 문자열이면 기존 값 삭제", example = "서울특별시 강남구 테헤란로 123")
        String address,

        @Size(max = 300, message = "상세주소는 300자 이하여야 합니다.")
        @Schema(description = "상세주소, 최대 300자; 생략/null/빈 문자열이면 기존 값 삭제", example = "401호")
        String addressDetail,

        @Pattern(
                regexp = "^$|^\\d{9,11}$",
                message = "연락처는 숫자 9~11자리여야 합니다.")
        @Schema(description = "연락처, 숫자 9~11자리; 생략/null/빈 문자열이면 기존 값 삭제", example = "01012345678")
        String phoneNo) {
}