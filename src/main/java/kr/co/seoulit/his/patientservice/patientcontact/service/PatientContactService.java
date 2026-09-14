package kr.co.seoulit.his.patientservice.patientcontact.service;

import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactCreateRequestDto;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactResponseDto;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactUpdateRequestDto;

public interface PatientContactService {

    List<PatientContactResponseDto> getContactList(
            UUID patientId,
            boolean includeInactive);

    PatientContactResponseDto createContact(
            UUID patientId,
            PatientContactCreateRequestDto request);

    PatientContactResponseDto updateContact(
            UUID patientId,
            UUID contactId,
            PatientContactUpdateRequestDto request);

    PatientContactResponseDto setPrimaryContact(
            UUID patientId,
            UUID contactId);

    PatientContactResponseDto deactivateContact(
            UUID patientId,
            UUID contactId);
}