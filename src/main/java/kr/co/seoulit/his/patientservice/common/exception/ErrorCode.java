package kr.co.seoulit.his.patientservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    PATIENT_NAME_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "일반환자는 환자명을 입력해야 합니다."),

    BIRTH_DATE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "일반환자는 생년월일을 입력해야 합니다."),

    RESIDENT_REG_NO_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "일반환자는 주민등록번호를 입력해야 합니다."),

    TEMP_REGISTER_REASON_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "임시환자는 임시등록 사유를 입력해야 합니다."),

    INVALID_RESIDENT_REG_NO(HttpStatus.BAD_REQUEST, "올바른 주민등록번호 형식이 아닙니다."),

    BIRTH_DATE_MISMATCH(HttpStatus.BAD_REQUEST, "주민등록번호와 생년월일이 일치하지 않습니다."),

    DUPLICATE_RESIDENT_REG_NO(HttpStatus.CONFLICT, "이미 등록된 주민등록번호입니다."),

    INVALID_PATIENT_STATUS(HttpStatus.BAD_REQUEST, "허용되지 않은 환자상태관리코드입니다."),

    DEATH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "사망 환자는 사망일시를 입력해야 합니다."),

    INVALID_DEATH_DATE(HttpStatus.BAD_REQUEST, "사망일시는 현재 시각보다 이후일 수 없습니다."),

    NOT_TEMPORARY_PATIENT(
            HttpStatus.BAD_REQUEST,
            "임시환자만 정식환자로 전환할 수 있습니다."),

    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "환자 정보를 찾을 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
