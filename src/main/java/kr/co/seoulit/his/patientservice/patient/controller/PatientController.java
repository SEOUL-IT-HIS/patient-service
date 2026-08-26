package kr.co.seoulit.his.patientservice.patient.controller;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patient.dto.*;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

  private final PatientService patientService;

  @PostMapping("/register")
  public ApiResponse<PatientRegisterResponseDto> createPatient(@Valid @RequestBody PatientDto dto) {
    return ApiResponse.success(patientService.createPatient(dto));
  }

  @GetMapping("/list")
  public ApiResponse<List<PatientListResponseDto>> getPatients(
      @RequestParam(required = false) String patientName,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate birthDate,
      @RequestParam(required = false) PatientStatus statusCd) {
    return ApiResponse.success(patientService.getPatients(patientName, birthDate, statusCd));
  }

  @PostMapping("/batch")
  public ApiResponse<List<PatientBatchResponseDto>> getPatientsByIds(
          @Valid @RequestBody PatientBatchRequestDto dto) {
    return ApiResponse.success(
            patientService.getPatientsByIds(dto.patientIds()));
  }

  @PostMapping("/duplicate-check")
  public ApiResponse<Boolean> checkResidentRegNoDuplicate(
      @Valid @RequestBody PatientDuplicateCheckDto dto) {
    return ApiResponse.success(patientService.isResidentRegNoDuplicate(dto.getResidentRegNo()));
  }

  @GetMapping("/{patientId}")
  public ApiResponse<PatientDetailResponseDto> getPatient(@PathVariable UUID patientId) {
    return ApiResponse.success(patientService.getPatient(patientId));
  }

  @PatchMapping("/{patientId}")
  public ApiResponse<PatientDetailResponseDto> updatePatientInfo(
          @PathVariable UUID patientId,
          @Valid @RequestBody PatientUpdateRequestDto dto) {
    return ApiResponse.success(patientService.updatePatientInfo(patientId, dto));
  }

  @PatchMapping("/{patientId}/death-status")
  public ApiResponse<PatientDetailResponseDto> updateDeathStatus(
      @PathVariable UUID patientId, @Valid @RequestBody PatientDeathUpdateRequestDto dto) {
    return ApiResponse.success(patientService.updateDeathStatus(patientId, dto));
  }

  @PatchMapping("/{patientId}/deactivate")
  public ApiResponse<PatientDetailResponseDto> deactivatePatient(@PathVariable UUID patientId) {
    return ApiResponse.success(patientService.deactivatePatient(patientId));
  }

  @GetMapping("/{patientId}/validation")
  public ApiResponse<PatientValidationResponseDto> validatePatient(@PathVariable UUID patientId) {
    return ApiResponse.success(patientService.validatePatient(patientId));
  }
}
