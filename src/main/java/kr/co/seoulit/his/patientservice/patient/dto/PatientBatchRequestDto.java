package kr.co.seoulit.his.patientservice.patient.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PatientBatchRequestDto(
        @NotEmpty(message = "환자 식별자 목록은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "환자는 한 번에 최대 100명까지 조회할 수 있습니다.")
        List<@NotNull(message = "환자 식별자는 null일 수 없습니다.") UUID> patientIds
) {
}