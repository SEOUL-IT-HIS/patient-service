package kr.co.seoulit.his.patientservice.patientRegister.service;

import kr.co.seoulit.his.patientservice.patientRegister.dto.PatientRegisterDto;
import kr.co.seoulit.his.patientservice.patientRegister.entity.PatientRegisterEntity;


public interface PatientRegisterService {
    PatientRegisterEntity insertPatientRegister(PatientRegisterDto patientRegisterDto);

    boolean checkDuplicateResidentRegNo(String residentRegNo);
}

