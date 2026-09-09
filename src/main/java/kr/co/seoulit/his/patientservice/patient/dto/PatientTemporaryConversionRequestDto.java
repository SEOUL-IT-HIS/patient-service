package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientTemporaryConversionRequestDto(

        @NotBlank(message = "환자명은 필수입니다.")
        @Size(
                min = 2,
                max = 100,
                message = "환자명은 2자 이상 100자 이하여야 합니다."
        )
        @Schema(description = "환자명", example = "홍길동")
        String patientName,

        @NotBlank(message = "주민등록번호는 필수입니다.")
        @Pattern(
                regexp = "\\d{13}",
                message = "주민등록번호는 숫자 13자리여야 합니다."
        )
        @Schema(description = "주민등록번호, 하이픈 없는 숫자 13자리")
        String residentRegNo,

        @NotNull(message = "생년월일은 필수입니다.")
        @PastOrPresent(
                message = "생년월일은 미래 날짜일 수 없습니다."
        )
        @Schema(description = "생년월일", example = "1990-01-01")
        LocalDate birthDate,

        @NotBlank(message = "성별은 필수입니다.")
        @Pattern(
                regexp = "^(01|02|03|04)$",
                message = "성별 코드는 01, 02, 03, 04 중 하나여야 합니다."
        )
        @Schema(description = "성별 공통코드 (01, 02, 03, 04)", example = "01")
        String genderCd
) {
}