package kr.co.seoulit.his.patientservice.common.commoncode.dto;

public record AdminApiResponse<T>(
        int code,
        String message,
        T data
) {
}