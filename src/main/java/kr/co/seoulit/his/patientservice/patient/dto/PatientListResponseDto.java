package kr.co.seoulit.his.patientservice.patient.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatientListResponseDto {

    private final UUID patientId;
    private final String patientName;
    private final String residentRegNo;
    private final LocalDate birthDate;
    private final String genderCd;
    private final PatientStatus statusCd;
    private final String tempPatientYn;
    private final String deathYn;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
