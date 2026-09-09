package kr.co.seoulit.his.patientservice.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Map;
import kr.co.seoulit.his.patientservice.patient.service.PatientProcedureService;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "환자 통계", description = "프로시저 기반 환자 수 조회")
@RestController
@RequestMapping("/api/patient/procedure")
@RequiredArgsConstructor
public class PatientProcedureController {
    private final PatientProcedureService service;

    @Operation(summary = "상태별 환자 수 조회", description = "Oracle 프로시저로 환자 수를 조회합니다. 성공 응답은 공통 응답 래퍼 없이 statusCd와 count를 직접 반환합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", useReturnTypeSchema = true,
                content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"statusCd\":\"ACTIVE\",\"count\":12}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 형식 또는 입력값 검증 실패",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":400,\"message\":\"요청값이 올바르지 않습니다. 파라미터: statusCd, 입력값: UNKNOWN\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류가 발생했습니다.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = kr.co.seoulit.his.patientservice.common.response.ApiResponse.class),
                        examples = @ExampleObject(value = "{\"code\":500,\"message\":\"서버 오류가 발생했습니다.\",\"data\":null}")))
    })
    @GetMapping("/count")
    public Map<String, Object> countPatients(
            @Parameter(description = "집계할 환자 상태, 기본값 ACTIVE") @RequestParam(name = "statusCd", defaultValue = "ACTIVE") PatientStatus statusCd) {
        return service.countPatients(statusCd);
    }
}
