package kr.co.seoulit.his.patientservice.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.time.LocalDate;
import kr.co.seoulit.his.patientservice.common.response.ApiResponse;
import kr.co.seoulit.his.patientservice.patient.dto.PatientPageResponseDto;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "환자 기본정보")
@RequestMapping("/api/patient/list/page")
public class PatientPageController {
    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "환자 목록 페이지 조회", description = "검색 결과를 15명씩 조회합니다. 등록일 내림차순, 동률이면 환자 ID 내림차순입니다. page는 1부터 시작하며 범위를 벗어나면 마지막 페이지를 반환합니다. 1 미만이면 400입니다. 결과가 없으면 items=[], page=1, size=15, totalElements=0, totalPages=0입니다. size 요청 파라미터는 지원하지 않습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"입력값이 올바르지 않습니다.\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    public ApiResponse<PatientPageResponseDto> getPatientPage(
            @RequestParam(name = "patientName", required = false) String patientName,
            @RequestParam(name = "birthDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(name = "statusCd", required = false) PatientStatus statusCd,
            @Parameter(description = "1부터 시작하는 페이지 번호", example = "1")
            @RequestParam(name = "page", defaultValue = "1") int page) {
        return ApiResponse.success(patientService.getPatientPage(patientName, birthDate, statusCd, page));
    }
}
