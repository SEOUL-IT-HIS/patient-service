package kr.co.seoulit.his.patientservice.patient.service;

import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    PatientRegisterResponseDto createPatient(PatientDto patientDto);

    boolean isResidentRegNoDuplicate(String residentRegNo);

    List<PatientListResponseDto> getPatients();

    PatientDetailResponseDto getPatient(UUID patientId);

    PatientValidationResponseDto validatePatient(UUID patientId);
}

