package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequestDto(

        @NotBlank(message = "환자명은 필수입니다.")
        @Size(
                min = 2,
                max = 100,
                message = "환자명은 2자 이상 100자 이하여야 합니다."
        )
        String patientName

) {
}