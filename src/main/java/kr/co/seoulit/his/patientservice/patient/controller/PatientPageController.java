package kr.co.seoulit.his.patientservice.patient.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Operation(summary = "환자 목록 페이지 조회", description = "검색 결과를 15명씩 조회합니다. 등록일 내림차순, 동률이면 환자 ID 내림차순입니다. page는 1부터 시작하며 범위를 벗어나면 마지막 페이지를 반환합니다. 1 미만이면 400입니다.")
    public ApiResponse<PatientPageResponseDto> getPatientPage(
            @RequestParam(name = "patientName", required = false) String patientName,
            @RequestParam(name = "birthDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(name = "statusCd", required = false) PatientStatus statusCd,
            @Parameter(description = "1부터 시작하는 페이지 번호", example = "1")
            @RequestParam(name = "page", defaultValue = "1") int page) {
        return ApiResponse.success(patientService.getPatientPage(patientName, birthDate, statusCd, page));
    }
}
