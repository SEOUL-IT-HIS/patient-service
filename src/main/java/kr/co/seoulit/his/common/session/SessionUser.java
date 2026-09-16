package kr.co.seoulit.his.common.session;

import lombok.Data;

/** Shared login user. Package and field names must match across services. */
@Data
public class SessionUser {
    private String accountId;
    private String accountStatus;
    private String empId;
    private String empNo;
    private String empName;
    private String deptCode;
    private String loginId;
    /** Comma-separated role codes. */
    private String roleCodes;
    /** Comma-separated menu codes. */
    private String menuCodes;
}
