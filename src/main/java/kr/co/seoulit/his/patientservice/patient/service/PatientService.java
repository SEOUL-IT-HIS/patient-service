package kr.co.seoulit.his.patientservice.patient.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patient.dto.PatientDeathUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientUpdateRequestDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import kr.co.seoulit.his.patientservice.patient.dto.PatientBatchResponseDto;

public interface PatientService {
    PatientRegisterResponseDto createPatient(PatientDto patientDto);

    boolean isResidentRegNoDuplicate(String residentRegNo);

    List<PatientListResponseDto> getPatients(
            String patientName, LocalDate birthDate, PatientStatus statusCd);

    List<PatientBatchResponseDto> getPatientsByIds(List<UUID> patientIds);

    PatientDetailResponseDto getPatient(UUID patientId);

    PatientDetailResponseDto updatePatientInfo(
            UUID patientId, PatientUpdateRequestDto dto);

    PatientDetailResponseDto updateDeathStatus(UUID patientId, PatientDeathUpdateRequestDto dto);

    PatientDetailResponseDto deactivatePatient(UUID patientId);

    PatientValidationResponseDto validatePatient(UUID patientId);
}
