package kr.co.seoulit.his.patientservice.patient.dto;

import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PatientRegisterResponseDto {

    private final Long patientId;
    private final String patientName;
    private final LocalDate birthDate;
    private final String genderCd;
    private final PatientStatus statusCd;
    private final LocalDateTime createdAt;

    public static PatientRegisterResponseDto from(
            PatientEntity patient
    ) {
        return new PatientRegisterResponseDto(
                patient.getPatientId(),
                patient.getPatientName(),
                patient.getBirthDate(),
                patient.getGenderCd(),
                patient.getStatusCd(),
                patient.getCreatedAt()
        );
    }
}