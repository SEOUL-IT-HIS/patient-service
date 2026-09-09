package kr.co.seoulit.his.patientservice.patientsafety.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

public record PatientSafetyCreateRequestDto(

        @NotBlank(message = "환자 안전정보 내용은 필수입니다.")
        @Schema(description = "안전정보 내용. 공백 불가, UTF-8 기준 최대 2000바이트 (글자 수 제한이 아님)", example = "라텍스 알레르기 있음")
        String safetyNote

) {
}