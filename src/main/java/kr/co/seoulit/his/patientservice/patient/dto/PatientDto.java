package kr.co.seoulit.his.patientservice.patient.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * [DTO] API 요청/응답용 객체
 * - Controller @RequestBody 로 받음
 */
@Getter
@Setter
@NoArgsConstructor
public class PatientDto {

    private String patientName;
    private LocalDate birthDate;
    private String residentRegNo;
    private String statusCd;

}

