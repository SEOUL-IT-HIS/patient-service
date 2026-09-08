package kr.co.seoulit.his.patientservice.patientsafety.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PatientSafetyResponseDto(

        UUID safetyInfoId,
        UUID patientId,
        String safetyNote,
        String activeYn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}