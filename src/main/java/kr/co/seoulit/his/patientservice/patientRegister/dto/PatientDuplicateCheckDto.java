package kr.co.seoulit.his.patientservice.patientRegister.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDuplicateCheckDto {

    @NotBlank(message = "주민등록번호는 필수입니다.")
    private String residentRegNo;
}