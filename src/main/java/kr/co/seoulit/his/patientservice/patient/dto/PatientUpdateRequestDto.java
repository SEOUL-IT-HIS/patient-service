package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatientUpdateRequestDto(
        @NotBlank(message = "환자명은 필수입니다.")
        @Size(min = 2, max = 100, message = "환자명은 2자 이상 100자 이하여야 합니다.")
        String patientName,

        @Pattern(
                regexp = "^$|^\\d{5}$",
                message = "우편번호는 숫자 5자리여야 합니다.")
        String zipCode,

        @Size(max = 300, message = "주소는 300자 이하여야 합니다.")
        String address,

        @Size(max = 300, message = "상세주소는 300자 이하여야 합니다.")
        String addressDetail,

        @Pattern(
                regexp = "^$|^\\d{9,11}$",
                message = "연락처는 숫자 9~11자리여야 합니다.")
        String phoneNo) {
}