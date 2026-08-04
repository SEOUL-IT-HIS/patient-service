package kr.co.seoulit.his.patientservice.patient.service;

import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;

import java.util.List;

public interface PatientService {
    PatientRegisterResponseDto createPatient(PatientDto patientDto);

    boolean isResidentRegNoDuplicate(String residentRegNo);

    List<PatientListResponseDto> getPatients();
}

