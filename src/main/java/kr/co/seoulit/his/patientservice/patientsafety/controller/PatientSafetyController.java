package kr.co.seoulit.his.patientservice.patientsafety.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyCreateRequestDto;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

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

@Tag(name = "환자 안전정보", description = "환자 안전정보 등록·조회·수정·비활성화·상단 고정 및 해제")
@RestController
@RequestMapping("/api/patient/{patientId}/safety-info")
@RequiredArgsConstructor
public class PatientSafetyController {

    private final PatientSafetyService patientSafetyService;

    @Operation(summary = "안전정보 상단 고정 및 해제", description = "활성 정보만 고정할 수 있으며 환자당 최대 2건입니다. 한도 초과 또는 비활성 정보는 409, 환자/정보 미존재는 404, 입력 오류는 400입니다. 같은 상태 재요청은 성공합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "고정 한도(2건) 초과 또는 비활성 안전정보의 고정 상태 변경",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":409,\"message\":\"안전정보는 최대 2건까지 고정할 수 있습니다. 기존 고정을 해제해 주세요.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{safetyInfoId}/pin")
    public ApiResponse<PatientSafetyResponseDto> setPinned(
            @PathVariable("patientId") UUID patientId,
            @PathVariable("safetyInfoId") UUID safetyInfoId,
            @Valid @RequestBody kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyPinRequestDto request) {
        return ApiResponse.success(patientSafetyService.setPinned(patientId, safetyInfoId, request.pinned()));
    }

    @Operation(summary = "환자 안전정보 등록", description = "공백이 아닌 UTF-8 기준 2000바이트 이하의 내용을 등록합니다. 최초 상태는 Y입니다. 환자 존재 여부만 검사합니다.")
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
    @PostMapping
    public ApiResponse<PatientSafetyResponseDto> createSafetyInfo(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Valid @RequestBody PatientSafetyCreateRequestDto request) {
        return ApiResponse.success(
                patientSafetyService.createSafetyInfo(patientId, request));
    }
    @Operation(summary = "환자 안전정보 목록 조회", description = "기본적으로 활성 정보만 조회하며 includeInactive=true이면 비활성 정보도 포함합니다. 활성 먼저, 고정 먼저, 생성시각 내림차순, 동률이면 안전정보 ID 내림차순입니다. 전체 배열을 반환하며 화면에서는 2건씩 펼칩니다.")
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
    @GetMapping
    public ApiResponse<List<PatientSafetyResponseDto>> getSafetyInfoList(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Parameter(description = "비활성 정보 포함 여부", example = "false")
            @RequestParam(
                    name = "includeInactive",
                    defaultValue = "false") boolean includeInactive) {

        return ApiResponse.success(
                patientSafetyService.getSafetyInfoList(
                        patientId,
                        includeInactive)
        );
    }

    @Operation(summary = "환자 안전정보 상세 조회", description = "해당 환자 소유의 안전정보 한 건을 조회합니다. 비활성 정보도 조회할 수 있습니다.")
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
    @GetMapping("/{safetyInfoId}")
    public ApiResponse<PatientSafetyResponseDto> getSafetyInfo(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Parameter(description = "해당 환자의 안전정보 UUID", example = "660e8400-e29b-41d4-a716-446655440001") @PathVariable("safetyInfoId") UUID safetyInfoId) {

        return ApiResponse.success(
                patientSafetyService.getSafetyInfo(
                        patientId,
                        safetyInfoId)
        );
    }

    @Operation(summary = "환자 안전정보 수정", description = "활성 정보만 수정할 수 있습니다. 내용은 공백일 수 없으며 UTF-8 기준 2000바이트 이하여야 합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청 데이터 형식이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 대상 정보를 찾을 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "비활성화된 환자 안전정보는 수정할 수 없습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":409,\"message\":\"비활성화된 환자 안전정보는 수정할 수 없습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{safetyInfoId}")
    public ApiResponse<PatientSafetyResponseDto> updateSafetyInfo(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Parameter(description = "해당 환자의 안전정보 UUID", example = "660e8400-e29b-41d4-a716-446655440001") @PathVariable("safetyInfoId") UUID safetyInfoId,
            @Valid @RequestBody PatientSafetyUpdateRequestDto request) {

        return ApiResponse.success(
                patientSafetyService.updateSafetyInfo(
                        patientId,
                        safetyInfoId,
                        request)
        );
    }

    @Operation(summary = "환자 안전정보 비활성화", description = "삭제하지 않고 ACTIVE_YN과 PINNED_YN을 N으로 변경합니다. 이미 비활성이면 수정시각을 변경하지 않고 성공 응답을 반환합니다.")
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
    @PatchMapping("/{safetyInfoId}/deactivate")
    public ApiResponse<PatientSafetyResponseDto> deactivateSafetyInfo(
            @Parameter(description = "환자 UUID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("patientId") UUID patientId,
            @Parameter(description = "해당 환자의 안전정보 UUID", example = "660e8400-e29b-41d4-a716-446655440001") @PathVariable("safetyInfoId") UUID safetyInfoId) {

        return ApiResponse.success(
                patientSafetyService.deactivateSafetyInfo(
                        patientId,
                        safetyInfoId)
        );
    }
}
