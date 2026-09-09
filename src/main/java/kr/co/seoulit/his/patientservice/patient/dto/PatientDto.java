package kr.co.seoulit.his.patientservice.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
    @Schema(description = "환자명; 일반환자 등록 시 필수", example = "홍길동")
    private String patientName;

    @PastOrPresent(message = "생년월일은 미래 날짜일 수 없습니다.")
    @Schema(description = "생년월일; 일반환자 등록 시 필수", example = "1990-01-01")
    private LocalDate birthDate;

    @Pattern(regexp = "\\d{13}", message = "주민등록번호는 숫자 13자리여야 합니다.")
    @Schema(description = "주민등록번호, 하이픈 없는 숫자 13자리; 일반환자 등록 시 필수")
    private String residentRegNo;

    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "^(01|02|03|04)$", message = "성별 코드는 01, 02, 03, 04 중 하나여야 합니다.")
    @Schema(description = "성별 공통코드 (01, 02, 03, 04)", example = "01")
    private String genderCd;

    @NotBlank(message = "임시환자 여부는 필수입니다.")
    @Pattern(regexp = "^[YN]$", message = "임시환자 여부는 Y 또는 N이어야 합니다.")
    @Schema(description = "임시환자 여부 (Y/N)", example = "N")
    private String tempPatientYn = "N";

    @Size(max = 200, message = "임시등록 사유는 200자 이하여야 합니다.")
    @Schema(description = "임시환자 등록 시 필수 사유", example = "신원 확인 중")
    private String tempRegisterReason;

    @Pattern(
            regexp = "^$|^\\d{5}$",
            message = "우편번호는 숫자 5자리여야 합니다.")
    @Schema(description = "우편번호, 숫자 5자리", example = "06236")
    private String zipCode;

    @Size(max = 300, message = "주소는 300자 이하여야 합니다.")
    @Schema(description = "기본주소, 최대 300자", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Size(max = 300, message = "상세주소는 300자 이하여야 합니다.")
    @Schema(description = "상세주소, 최대 300자", example = "401호")
    private String addressDetail;

    @Pattern(
            regexp = "^$|^\\d{9,11}$",
            message = "연락처는 숫자 9~11자리여야 합니다.")
    @Schema(description = "연락처, 숫자 9~11자리", example = "01012345678")
    private String phoneNo;
}
