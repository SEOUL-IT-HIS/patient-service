package kr.co.seoulit.his.patientservice.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patient.dto.PatientBatchRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientBatchResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDeathUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDuplicateCheckDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientTemporaryConversionRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
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
@Tag(name = "환자 기본정보", description = "환자 등록·검색·수정·상태 및 주소·연락처 관리")
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "환자 등록", description = "일반환자 또는 임시환자를 등록합니다. 주소·연락처는 선택값이며 일반환자는 환자명·생년월일·주민등록번호가 필요합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 주민등록번호입니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":409,\"message\":\"이미 등록된 주민등록번호입니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PostMapping("/register")
    public ApiResponse<PatientRegisterResponseDto> createPatient(
            @Valid @RequestBody PatientDto dto) {
        return ApiResponse.success(patientService.createPatient(dto));
    }

    @Operation(summary = "환자 검색 및 목록 조회", description = "환자명·생년월일·상태로 검색합니다. 주민등록번호는 마스킹하여 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @GetMapping("/list")
    public ApiResponse<List<PatientListResponseDto>> getPatients(
            @Parameter(description = "환자명 검색어") @RequestParam(required = false) String patientName,
            @Parameter(description = "생년월일 (yyyy-MM-dd)", example = "1990-01-01") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate birthDate,
            @Parameter(description = "환자 상태 (ACTIVE 또는 INACTIVE)") @RequestParam(required = false) PatientStatus statusCd) {
        return ApiResponse.success(patientService.getPatients(patientName, birthDate, statusCd));
    }

    @Operation(summary = "환자 배치 조회", description = "최대 100개의 환자 UUID로 조회합니다. 중복 ID는 제거하고 존재하지 않는 환자는 결과에서 제외합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PostMapping("/batch")
    public ApiResponse<List<PatientBatchResponseDto>> getPatientsByIds(
            @Valid @RequestBody PatientBatchRequestDto dto) {
        return ApiResponse.success(
                patientService.getPatientsByIds(dto.patientIds()));
    }

    @Operation(summary = "주민등록번호 중복 확인", description = "중복이면 true를 반환합니다. excludePatientId로 본인을 검사 대상에서 제외할 수 있습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PostMapping("/duplicate-check")
    public ApiResponse<Boolean> checkResidentRegNoDuplicate(
            @Valid @RequestBody PatientDuplicateCheckDto dto) {
        return ApiResponse.success(
                patientService.isResidentRegNoDuplicate(
                        dto.getResidentRegNo(),
                        dto.getExcludePatientId()));
    }

    @Operation(summary = "환자 상세 조회", description = "주소·연락처를 포함한 환자 상세정보를 조회합니다. 주민등록번호는 마스킹됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @GetMapping("/{patientId}")
    public ApiResponse<PatientDetailResponseDto> getPatient(@Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId) {
        return ApiResponse.success(patientService.getPatient(patientId));
    }

    @Operation(summary = "환자정보 및 주소·연락처 수정", description = "환자명은 필수입니다. 주소·연락처는 생략, null 또는 빈 문자열이면 기존 값이 삭제됩니다. 대표 지정 및 개별 비활성화는 지원하지 않습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{patientId}")
    public ApiResponse<PatientDetailResponseDto> updatePatientInfo(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Valid @RequestBody PatientUpdateRequestDto dto) {
        return ApiResponse.success(patientService.updatePatientInfo(patientId, dto));
    }

    @Operation(summary = "임시환자 정규환자 전환", description = "임시환자의 필수 인적사항을 검증하여 정규환자로 전환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 주민등록번호입니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":409,\"message\":\"이미 등록된 주민등록번호입니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{patientId}/convert-from-temporary")
    public ApiResponse<PatientDetailResponseDto> convertTemporaryPatient(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Valid @RequestBody PatientTemporaryConversionRequestDto dto) {
        return ApiResponse.success(
                patientService.convertTemporaryPatient(patientId, dto));
    }

    @Operation(summary = "환자 사망정보 수정", description = "사망 여부와 사망일시를 변경합니다. 사망 처리 시 환자를 비활성화합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{patientId}/death-status")
    public ApiResponse<PatientDetailResponseDto> updateDeathStatus(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId, @Valid @RequestBody PatientDeathUpdateRequestDto dto) {
        return ApiResponse.success(patientService.updateDeathStatus(patientId, dto));
    }

    @Operation(summary = "환자 비활성화", description = "환자 상태를 INACTIVE로 변경합니다. 주소·연락처 개별 비활성화가 아닙니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{patientId}/deactivate")
    public ApiResponse<PatientDetailResponseDto> deactivatePatient(@Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId) {
        return ApiResponse.success(patientService.deactivatePatient(patientId));
    }

    @Operation(summary = "환자 활성화", description = "환자 상태를 ACTIVE로 변경합니다. 사망 환자는 먼저 사망정보를 해제해야 합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{patientId}/activate")
    public ApiResponse<PatientDetailResponseDto> activatePatient(@Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId) {
        return ApiResponse.success(patientService.activatePatient(patientId));
    }

    @Operation(summary = "환자 유효성 확인", description = "존재하며 ACTIVE이고 사망 여부가 N인 환자만 valid=true입니다. 환자가 없어도 200과 valid=false를 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @GetMapping("/{patientId}/validation")
    public ApiResponse<PatientValidationResponseDto> validatePatient(@Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId) {
        return ApiResponse.success(patientService.validatePatient(patientId));
    }
}
