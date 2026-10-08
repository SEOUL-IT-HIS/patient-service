-- 이름을 입력하지 않은 임시환자를 저장할 수 있도록 환자명 컬럼을 nullable로 변경한다.
ALTER TABLE PATIENT MODIFY (PATIENT_NAME NULL);
