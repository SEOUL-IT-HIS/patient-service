package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record PatientDeathUpdateRequestDto(

        @NotBlank(message = "사망 여부는 필수입니다.")
        @Pattern(
                regexp = "^[YN]$",
                message = "사망 여부는 Y 또는 N이어야 합니다."
        )
        String deathYn,

        LocalDateTime deathDtm

) {
}