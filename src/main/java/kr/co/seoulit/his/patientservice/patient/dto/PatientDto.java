package kr.co.seoulit.his.patientservice.patient.dto;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.*;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * [DTO] API 요청/응답용 객체
 * - Controller @RequestBody 로 받음
 */
@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    @NotBlank(message = "환자명은 필수입니다.")
    @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
    private String patientName;

    @NotNull(message = "생년월일은 필수입니다.")
    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    private LocalDate birthDate;

    @NotBlank(message = "주민등록번호는 필수입니다.")
    @Pattern(
            regexp = "\\d{13}",
            message = "주민등록번호는 숫자 13자리여야 합니다."
    )
    private String residentRegNo;

    @NotNull(message = "환자 상태는 필수입니다.")
    private PatientStatus statusCd;

}

