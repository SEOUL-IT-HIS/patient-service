package kr.co.seoulit.his.patientservice.patientsafety.service;

import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyCreateRequestDto;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyResponseDto;
import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyUpdateRequestDto;

public interface PatientSafetyService {

    PatientSafetyResponseDto createSafetyInfo(
            UUID patientId,
            PatientSafetyCreateRequestDto request);

    List<PatientSafetyResponseDto> getSafetyInfoList(
            UUID patientId,
            boolean includeInactive);

    PatientSafetyResponseDto getSafetyInfo(
            UUID patientId,
            UUID safetyInfoId);

    PatientSafetyResponseDto updateSafetyInfo(
            UUID patientId,
            UUID safetyInfoId,
            PatientSafetyUpdateRequestDto request);

    PatientSafetyResponseDto deactivateSafetyInfo(
            UUID patientId,
            UUID safetyInfoId);
}