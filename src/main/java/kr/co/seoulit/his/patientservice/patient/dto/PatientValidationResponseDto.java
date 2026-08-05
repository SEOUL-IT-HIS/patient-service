package kr.co.seoulit.his.patientservice.patient.dto;

public record PatientValidationResponseDto(
        Long patientId,
        boolean valid
) {
}