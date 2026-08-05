package kr.co.seoulit.his.patientservice.patient.controller;

import jakarta.validation.constraints.Positive;
import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/patients")
public class InternalPatientController {

    private final PatientService patientService;

    @GetMapping("/{patientId}/validation")
    public ApiResponse<PatientValidationResponseDto> validatePatient(
            @PathVariable @Positive Long patientId
    ) {
        return ApiResponse.success(
                patientService.validatePatient(patientId)
        );
    }
}