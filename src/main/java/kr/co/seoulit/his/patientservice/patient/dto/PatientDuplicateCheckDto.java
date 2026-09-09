package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PatientDuplicateCheckDto {

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(regexp = "\\d{13}", message = "주민등록번호는 숫자 13자리여야 합니다.")
    @Schema(description = "주민등록번호, 하이픈 없는 숫자 13자리")
    private String residentRegNo;

    @Schema(description = "중복 검사에서 제외할 환자 UUID")
    private UUID excludePatientId;
}
