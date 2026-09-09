-- patient 계정으로 접속한 뒤 실행하세요.
CREATE OR REPLACE PROCEDURE PROC_PATIENT_COUNT (
    p_status_cd IN VARCHAR2,
    p_count OUT NUMBER
)
IS
BEGIN
    SELECT COUNT(*)
      INTO p_count
      FROM PATIENT
     WHERE STATUS_CD = p_status_cd;
END;
/
