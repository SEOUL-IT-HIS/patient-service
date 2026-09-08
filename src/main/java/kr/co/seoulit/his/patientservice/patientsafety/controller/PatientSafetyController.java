package kr.co.seoulit.his.patientservice.patientsafety.controller;

import jakarta.validation.Valid;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyCreateRequestDto;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyResponseDto;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patientsafety.service.PatientSafetyService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "환자 안전정보", description = "환자 안전정보 관리 API")
@RestController
@RequestMapping("/api/patient/{patientId}/safety-info")
@RequiredArgsConstructor
public class PatientSafetyController {

    private final PatientSafetyService patientSafetyService;

    @Operation(summary = "환자 안전정보 등록",
            description = "UTF-8 기준 2000바이트 이하의 안전정보를 등록합니다.")
    @PostMapping
    public ApiResponse<PatientSafetyResponseDto> createSafetyInfo(
            @PathVariable("patientId") UUID patientId,
            @Valid @RequestBody PatientSafetyCreateRequestDto request) {
        return ApiResponse.success(
                patientSafetyService.createSafetyInfo(patientId, request));
    }
    @Operation(
            summary = "환자 안전정보 목록 조회",
            description = "기본적으로 활성 정보만 조회합니다. "
                    + "includeInactive=true이면 비활성 정보도 포함합니다.")
    @GetMapping
    public ApiResponse<List<PatientSafetyResponseDto>> getSafetyInfoList(
            @PathVariable("patientId") UUID patientId,
            @RequestParam(
                    name = "includeInactive",
                    defaultValue = "false") boolean includeInactive) {

        return ApiResponse.success(
                patientSafetyService.getSafetyInfoList(
                        patientId,
                        includeInactive)
        );
    }

@Operation(
            summary = "환자 안전정보 상세 조회",
            description = "해당 환자의 안전정보 한 건을 조회합니다. "
                    + "비활성 정보도 조회할 수 있습니다.")
    @GetMapping("/{safetyInfoId}")
    public ApiResponse<PatientSafetyResponseDto> getSafetyInfo(
            @PathVariable("patientId") UUID patientId,
            @PathVariable("safetyInfoId") UUID safetyInfoId) {

        return ApiResponse.success(
                patientSafetyService.getSafetyInfo(
                        patientId,
                        safetyInfoId)
        );
    }

@Operation(
            summary = "환자 안전정보 수정",
            description = "활성 정보의 내용을 수정합니다. "
                    + "내용은 공백일 수 없으며 UTF-8 기준 2000바이트 이하여야 합니다.")
    @PatchMapping("/{safetyInfoId}")
    public ApiResponse<PatientSafetyResponseDto> updateSafetyInfo(
            @PathVariable("patientId") UUID patientId,
            @PathVariable("safetyInfoId") UUID safetyInfoId,
            @Valid @RequestBody PatientSafetyUpdateRequestDto request) {

        return ApiResponse.success(
                patientSafetyService.updateSafetyInfo(
                        patientId,
                        safetyInfoId,
                        request)
        );
    }

@Operation(
            summary = "환자 안전정보 비활성화",
            description = "데이터를 삭제하지 않고 ACTIVE_YN을 N으로 변경합니다. "
                    + "이미 비활성 상태이면 변경 없이 성공 응답을 반환합니다.")
    @PatchMapping("/{safetyInfoId}/deactivate")
    public ApiResponse<PatientSafetyResponseDto> deactivateSafetyInfo(
            @PathVariable("patientId") UUID patientId,
            @PathVariable("safetyInfoId") UUID safetyInfoId) {

        return ApiResponse.success(
                patientSafetyService.deactivateSafetyInfo(
                        patientId,
                        safetyInfoId)
        );
    }
}