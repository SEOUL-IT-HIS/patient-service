package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class PatientDuplicateCheckDto {

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(
            regexp = "\\d{13}",
            message = "주민등록번호는 숫자 13자리여야 합니다."
    )
    private String residentRegNo;
}