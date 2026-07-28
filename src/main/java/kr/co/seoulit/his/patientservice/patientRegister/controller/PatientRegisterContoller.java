package kr.co.seoulit.his.patientservice.patientRegister.controller;


import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patientRegister.dto.PatientRegisterDto;
import kr.co.seoulit.his.patientservice.patientRegister.entity.PatientRegisterEntity;
import kr.co.seoulit.his.patientservice.patientRegister.service.PatientRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import kr.co.seoulit.his.patientservice.patientRegister.dto.PatientDuplicateCheckDto;


@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientRegisterContoller {

    private final PatientRegisterService patientRegisterService;

    @PostMapping("/register")
    public ApiResponse<PatientRegisterEntity> createPatient(@RequestBody PatientRegisterDto dto) {
        return ApiResponse.success(patientRegisterService.insertPatientRegister(dto));
    }

    @PostMapping("/duplicate-check")
    public ApiResponse<Boolean> checkDuplicate(
            @Valid @RequestBody PatientDuplicateCheckDto dto
    ) {
        boolean duplicated =
                patientRegisterService.checkDuplicateResidentRegNo(
                        dto.getResidentRegNo()
                );

        return ApiResponse.success(duplicated);
    }

}
