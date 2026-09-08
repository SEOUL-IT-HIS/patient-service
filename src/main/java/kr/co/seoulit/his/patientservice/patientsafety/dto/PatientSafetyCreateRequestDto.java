package kr.co.seoulit.his.patientservice.patientsafety.dto;

import jakarta.validation.constraints.NotBlank;

public record PatientSafetyCreateRequestDto(

        @NotBlank(message = "환자 안전정보 내용은 필수입니다.")
        String safetyNote

) {
}