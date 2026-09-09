package kr.co.seoulit.his.patientservice.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 공통 API 응답 포맷 (개발표준가이드 11.3) { "code": 200, "message": "SUCCESS", "data": {} }
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int code;
    @Schema(description = "성공 시 SUCCESS, 실패 시 오류 메시지", example = "SUCCESS")
    private final String message;
    @Schema(description = "API별 응답 데이터. 오류 시 null")
    private final T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "SUCCESS", data);
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", data); // user
    }
}
