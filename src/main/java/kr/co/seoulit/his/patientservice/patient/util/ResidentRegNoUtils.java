package kr.co.seoulit.his.patientservice.patient.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;

public final class ResidentRegNoUtils {

  private ResidentRegNoUtils() {}

  public static LocalDate extractBirthDate(String residentRegNo) {
    if (residentRegNo == null || !residentRegNo.matches("\\d{13}")) {
      throw new BusinessException(ErrorCode.INVALID_RESIDENT_REG_NO);
    }

    int yearPart = Integer.parseInt(residentRegNo.substring(0, 2));
    int month = Integer.parseInt(residentRegNo.substring(2, 4));
    int day = Integer.parseInt(residentRegNo.substring(4, 6));
    char typeCode = residentRegNo.charAt(6);

    int century =
        switch (typeCode) {
          case '1', '2', '5', '6' -> 1900;
          case '3', '4', '7', '8' -> 2000;
          default -> throw new BusinessException(ErrorCode.INVALID_RESIDENT_REG_NO);
        };

    try {
      return LocalDate.of(century + yearPart, month, day);
    } catch (DateTimeException exception) {
      throw new BusinessException(ErrorCode.INVALID_RESIDENT_REG_NO);
    }
  }
}
