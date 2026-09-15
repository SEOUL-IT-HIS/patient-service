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
    @Schema(description = "환자명; 일반환자 필수. 입력 문자열 기준 2~100자 검증 후 trim, 이후 길이 재검증 없음. 임시환자 생략·null 시 이름 자동 생성", example = "홍길동")
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
    @Schema(description = "임시환자 여부. 생략 시 N, 명시적 null·빈 문자열 불가", example = "N", defaultValue = "N", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tempPatientYn = "N";

    @Size(max = 200, message = "임시등록 사유는 200자 이하여야 합니다.")
    @Schema(description = "임시환자 필수 사유. 입력 문자열 기준 최대 200자 검증 후 trim", example = "신원 확인 중")
    private String tempRegisterReason;
}
