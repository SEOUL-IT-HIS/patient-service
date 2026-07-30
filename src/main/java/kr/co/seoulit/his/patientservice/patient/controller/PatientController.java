package kr.co.seoulit.his.patientservice.patient.controller;


import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDuplicateCheckDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ApiResponse<PatientEntity> createPatient(@RequestBody PatientDto dto) {
        return ApiResponse.success(patientService.createPatient(dto));
    }

    @GetMapping("/list")
    public ApiResponse<List<PatientListResponseDto>> getPatients() {
        return ApiResponse.success(patientService.getPatients());
    }

    @PostMapping("/duplicate-check")
    public ApiResponse<Boolean> checkResidentRegNoDuplicate(
            @Valid @RequestBody PatientDuplicateCheckDto dto
    ) {
        return ApiResponse.success(
                patientService.isResidentRegNoDuplicate(dto.getResidentRegNo())
        );
    }
}
