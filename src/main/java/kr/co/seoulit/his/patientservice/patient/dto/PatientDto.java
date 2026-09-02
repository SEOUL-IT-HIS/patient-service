package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import lombok.*;

/**
 * [DTO] API 요청/응답용 객체 - Controller @RequestBody 로 받음
 */
@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
    private String patientName;

    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    private LocalDate birthDate;

    @Pattern(regexp = "\\d{13}", message = "주민등록번호는 숫자 13자리여야 합니다.")
    private String residentRegNo;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(01|02|03|04)$", message = "성별 코드는 01, 02, 03, 04 중 하나여야 합니다.")
    private String genderCd;

    @NotBlank(message = "임시환자 여부는 필수입니다.")
    @Pattern(regexp = "^[YN]$", message = "임시환자 여부는 Y 또는 N이어야 합니다.")
    private String tempPatientYn = "N";

    @Size(max = 200, message = "임시등록 사유는 200자 이하여야 합니다.")
    private String tempRegisterReason;

    @Pattern(
            regexp = "^$|^\\d{5}$",
            message = "우편번호는 숫자 5자리여야 합니다.")
    private String zipCode;

    @Size(max = 300, message = "주소는 300자 이하여야 합니다.")
    private String address;

    @Size(max = 300, message = "상세주소는 300자 이하여야 합니다.")
    private String addressDetail;

    @Pattern(
            regexp = "^$|^\\d{9,11}$",
            message = "연락처는 숫자 9~11자리여야 합니다.")
    private String phoneNo;
}
