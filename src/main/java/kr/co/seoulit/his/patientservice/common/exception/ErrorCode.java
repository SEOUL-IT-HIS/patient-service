package kr.co.seoulit.his.patientservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(
            HttpStatus.BAD_REQUEST,
            "입력값이 올바르지 않습니다."
    ),

    INVALID_RESIDENT_REG_NO(
            HttpStatus.BAD_REQUEST,
            "올바른 주민등록번호 형식이 아닙니다."
    ),

    BIRTH_DATE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "주민등록번호와 생년월일이 일치하지 않습니다."
    ),

    DUPLICATE_RESIDENT_REG_NO(
            HttpStatus.CONFLICT,
            "이미 등록된 주민등록번호입니다."
    ),

    INVALID_PATIENT_STATUS(
            HttpStatus.BAD_REQUEST,
            "허용되지 않은 환자 상태입니다."
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 오류가 발생했습니다."
    );

    private final HttpStatus httpStatus;
    private final String message;
}