package kr.co.seoulit.his.patientservice.patient.dto;

import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PatientListResponseDto {

    private final UUID patientId;
    private final String patientName;
    private final String residentRegNo;
    private final LocalDate birthDate;
    private final String genderCd;
    private final PatientStatus statusCd;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PatientListResponseDto from(
            PatientEntity patient
    ) {
        return new PatientListResponseDto(
                patient.getPatientId(),
                patient.getPatientName(),
                maskResidentRegNo(patient.getResidentRegNo()),
                patient.getBirthDate(),
                patient.getGenderCd(),
                patient.getStatusCd(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }

    /**
     * 주민등록번호 마스킹
     * 예: 0008134123456 → 000813-4******
     */
    private static String maskResidentRegNo(String residentRegNo) {
        if (residentRegNo == null || residentRegNo.isBlank()) {
            return "";
        }

        String digits = residentRegNo.replaceAll("[^0-9]", "");
        if (digits.length() < 7) {
            return residentRegNo;
        }

        return digits.substring(0, 6)
                + "-"
                + digits.charAt(6)
                + "******";
    }
}
