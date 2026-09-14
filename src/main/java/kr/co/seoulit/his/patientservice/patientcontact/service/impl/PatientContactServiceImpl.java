package kr.co.seoulit.his.patientservice.patientcontact.service.impl;

import java.util.List;
import java.util.UUID;

import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactResponseDto;
import kr.co.seoulit.his.patientservice.patientcontact.mapper.PatientContactMapper;
import kr.co.seoulit.his.patientservice.patientcontact.repository.PatientContactRepository;
import kr.co.seoulit.his.patientservice.patientcontact.service.PatientContactService;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactCreateRequestDto;
import kr.co.seoulit.his.patientservice.patientcontact.entity.PatientContactEntity;
import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactUpdateRequestDto;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientContactServiceImpl
        implements PatientContactService {

    private final PatientRepository patientRepository;
    private final PatientContactRepository patientContactRepository;
    private final PatientContactMapper patientContactMapper;

    @Override
    public List<PatientContactResponseDto> getContactList(UUID patientId, boolean includeInactive) {

        validatePatientExists(patientId);

        return patientContactRepository.findOrdered(patientId, includeInactive).stream().map(patientContactMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public PatientContactResponseDto createContact(UUID patientId, PatientContactCreateRequestDto request) {

        patientRepository.lockPatient(patientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        ContactValues values = normalizeAndValidate(request.zipCode(), request.address(), request.addressDetail(), request.phoneNo());

        boolean primary = patientContactRepository.findByPatientIdAndPrimaryYnAndActiveYn(patientId, "Y", "Y").isEmpty();

        PatientContactEntity contact = PatientContactEntity.create(patientId, values.zipCode(), values.address(), values.addressDetail(), values.phoneNo(), primary);

        PatientContactEntity savedContact = patientContactRepository.saveAndFlush(contact);

        return patientContactMapper.toResponseDto(savedContact);
    }

    @Override
    @Transactional
    public PatientContactResponseDto updateContact(UUID patientId, UUID contactId, PatientContactUpdateRequestDto request) {

        patientRepository.lockPatient(patientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        PatientContactEntity contact = patientContactRepository.findByContactIdAndPatientId(contactId, patientId).orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_CONTACT_NOT_FOUND));

        if (!contact.isActive()) {
            throw new BusinessException(ErrorCode.PATIENT_CONTACT_INACTIVE);
        }

        ContactValues values = normalizeAndValidate(request.zipCode(), request.address(), request.addressDetail(), request.phoneNo());

        contact.update(values.zipCode(), values.address(), values.addressDetail(), values.phoneNo());

        patientContactRepository.flush();

        return patientContactMapper.toResponseDto(contact);
    }

    @Override
    @Transactional
    public PatientContactResponseDto setPrimaryContact(UUID patientId, UUID contactId) {

        patientRepository.lockPatient(patientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        PatientContactEntity contact = patientContactRepository.findByContactIdAndPatientId(contactId, patientId).orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_CONTACT_NOT_FOUND));

        if (!contact.isActive()) {
            throw new BusinessException(ErrorCode.PATIENT_CONTACT_INACTIVE);
        }

        PatientContactEntity currentPrimary = patientContactRepository.findByPatientIdAndPrimaryYnAndActiveYn(patientId, "Y", "Y").orElse(null);

        if (currentPrimary != null && !currentPrimary.getContactId().equals(contactId)) {
            currentPrimary.clearPrimary();
        }

        contact.setPrimary();

        patientContactRepository.flush();

        return patientContactMapper.toResponseDto(contact);
    }

    @Override
    @Transactional
    public PatientContactResponseDto deactivateContact(UUID patientId, UUID contactId) {

        patientRepository.lockPatient(patientId).orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_NOT_FOUND));

        PatientContactEntity contact = patientContactRepository.findByContactIdAndPatientId(contactId, patientId).orElseThrow(() -> new BusinessException(ErrorCode.PATIENT_CONTACT_NOT_FOUND));

        if (!contact.isActive()) {
            return patientContactMapper.toResponseDto(contact);
        }

        if (contact.isPrimary()) {
            throw new BusinessException(
                    ErrorCode.PATIENT_CONTACT_PRIMARY_DEACTIVATION);
        }

        long activeContactCount = patientContactRepository.countByPatientIdAndActiveYn(patientId, "Y");

        if (activeContactCount <= 1) {
            throw new BusinessException(ErrorCode.PATIENT_CONTACT_LAST_ACTIVE);
        }

        contact.deactivate();

        patientContactRepository.flush();

        return patientContactMapper.toResponseDto(contact);
    }

    private void validatePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
    }

    private ContactValues normalizeAndValidate(String zipCode, String address, String addressDetail, String phoneNo) {

        ContactValues values = new ContactValues(normalize(zipCode), normalize(address), normalize(addressDetail), normalize(phoneNo));

        if (values.zipCode() == null && values.address() == null && values.addressDetail() == null && values.phoneNo() == null) {

            throw new BusinessException(ErrorCode.INVALID_INPUT, "주소 또는 연락처를 하나 이상 입력해야 합니다.");
        }

        return values;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private record ContactValues(String zipCode, String address, String addressDetail, String phoneNo) {
    }
}
