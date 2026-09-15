package kr.co.seoulit.his.patientservice.patientcontact.controller;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import kr.co.seoulit.his.patientservice.common.config.ApiErrorSchema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactResponseDto;
import kr.co.seoulit.his.patientservice.patientcontact.service.PatientContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactCreateRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactUpdateRequestDto;
import org.springframework.web.bind.annotation.PatchMapping;

@Tag(
        name = "환자 주소·연락처",
        description = "환자 주소·연락처 등록·조회·수정·대표 지정·비활성화")
@RestController
@RequestMapping("/api/patient/{patientId}/contacts")
@RequiredArgsConstructor
public class PatientContactController {

    private final PatientContactService patientContactService;

    @Operation(summary = "환자 주소·연락처 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "UUID 또는 includeInactive 형식 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 미존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @GetMapping
    public ApiResponse<List<PatientContactResponseDto>> getContactList(
            @PathVariable UUID patientId,
            @RequestParam(
                    name = "includeInactive",
                    defaultValue = "false") boolean includeInactive) {

        return ApiResponse.success(
                patientContactService.getContactList(
                        patientId,
                        includeInactive
                )
        );
    }

    @Operation(summary = "환자 주소·연락처 등록", description = "네 값 중 하나 이상 필요합니다. 형식·길이 검증 후 trim하고 빈 값은 null로 저장합니다. 활성 대표가 없으면 자동 대표가 됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 또는 요청 본문 형식 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 미존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PostMapping
    public ApiResponse<PatientContactResponseDto> createContact(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientContactCreateRequestDto request) {

        return ApiResponse.success(
                patientContactService.createContact(
                        patientId,
                        request
                )
        );
    }

    @Operation(summary = "환자 주소·연락처 수정", description = "활성 연락처의 네 필드를 교체합니다. 생략·null·빈 문자열은 기존 값을 null로 초기화합니다. 네 값이 모두 비면 400입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "입력값 검증 또는 UUID 형식 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 연락처 미존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "비활성 연락처 수정 불가",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":409,\"message\":\"비활성화된 환자 연락처 정보는 수정하거나 대표로 지정할 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{contactId}")
    public ApiResponse<PatientContactResponseDto> updateContact(
            @PathVariable UUID patientId,
            @PathVariable UUID contactId,
            @Valid @RequestBody PatientContactUpdateRequestDto request) {

        return ApiResponse.success(
                patientContactService.updateContact(
                        patientId,
                        contactId,
                        request
                )
        );
    }

    @Operation(summary = "대표 주소·연락처 지정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "UUID 형식 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 연락처 미존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "비활성 연락처를 대표로 지정할 수 없음",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":409,\"message\":\"비활성화된 환자 연락처 정보는 수정하거나 대표로 지정할 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{contactId}/primary")
    public ApiResponse<PatientContactResponseDto> setPrimaryContact(
            @PathVariable UUID patientId,
            @PathVariable UUID contactId) {

        return ApiResponse.success(
                patientContactService.setPrimaryContact(
                        patientId,
                        contactId
                )
        );
    }

    @Operation(summary = "환자 주소·연락처 비활성화")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "UUID 형식 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "환자 또는 해당 환자의 연락처 미존재",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":404,\"message\":\"환자 정보를 찾을 수 없습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "대표 또는 마지막 활성 연락처 비활성화 불가",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":409,\"message\":\"대표 환자 연락처 정보는 다른 활성 연락처를 대표로 지정한 뒤 비활성화할 수 있습니다.\",\"data\":null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorSchema.class),
                            examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @PatchMapping("/{contactId}/deactivate")
    public ApiResponse<PatientContactResponseDto> deactivateContact(
            @PathVariable UUID patientId,
            @PathVariable UUID contactId) {

        return ApiResponse.success(
                patientContactService.deactivateContact(
                        patientId,
                        contactId
                )
        );
    }
}
