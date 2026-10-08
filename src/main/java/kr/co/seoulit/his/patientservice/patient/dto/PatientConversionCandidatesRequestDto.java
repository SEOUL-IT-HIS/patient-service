package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record PatientConversionCandidatesRequestDto(
        @NotBlank(message = "주민등록번호는 필수입니다.")
        @Pattern(regexp = "\\d{13}", message = "주민등록번호는 숫자 13자리여야 합니다.")
        String residentRegNo,

        @NotNull(message = "전환 대상 환자 ID는 필수입니다.")
        UUID excludePatientId
) {
}
