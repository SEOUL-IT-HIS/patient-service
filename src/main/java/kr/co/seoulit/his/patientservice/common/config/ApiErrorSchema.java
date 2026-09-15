package kr.co.seoulit.his.patientservice.common.config;

import io.swagger.v3.oas.annotations.media.Schema;

/** OpenAPI documentation only; runtime responses are produced by GlobalExceptionHandler. */
@Schema(description = "공통 오류 응답")
public record ApiErrorSchema(
        @Schema(description = "HTTP 오류 상태 코드", example = "400") int code,
        @Schema(description = "오류 원인 메시지", example = "입력값이 올바르지 않습니다.") String message,
        @Schema(description = "오류 시 항상 null", types = {"null"}) Object data) {
}
