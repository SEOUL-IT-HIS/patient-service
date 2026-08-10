package kr.co.seoulit.his.patientservice.patient.service.impl;

import kr.co.seoulit.his.patientservice.patient.dto.PatientListResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import kr.co.seoulit.his.patientservice.patient.mapper.PatientMapper;
import kr.co.seoulit.his.patientservice.patient.repository.PatientRepository;
import kr.co.seoulit.his.patientservice.patient.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.seoulit.his.patientservice.patient.util.ResidentRegNoUtils;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import kr.co.seoulit.his.patientservice.common.exception.BusinessException;
import kr.co.seoulit.his.patientservice.common.exception.ErrorCode;
import kr.co.seoulit.his.patientservice.patient.dto.PatientRegisterResponseDto;
import kr.co.seoulit.his.patientservice.patient.dto.PatientValidationResponseDto;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import kr.co.seoulit.his.patientservice.patient.dto.PatientDetailResponseDto;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientRegisterResponseDto createPatient(
            PatientDto dto
    ) {

        LocalDate birthDateFromResidentRegNo =
                ResidentRegNoUtils.extractBirthDate(
                        dto.getResidentRegNo()
                );

        if (!birthDateFromResidentRegNo.equals(dto.getBirthDate())) {
            throw new BusinessException(
                    ErrorCode.BIRTH_DATE_MISMATCH
            );
        }

        if (patientRepository.existsByResidentRegNo(
                dto.getResidentRegNo()
        )) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESIDENT_REG_NO
            );
        }

        PatientEntity entity = PatientMapper.toEntity(dto);
        PatientEntity savedPatient = patientRepository.save(entity);

        return PatientRegisterResponseDto.from(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentRegNoDuplicate(String residentRegNo) {
        ResidentRegNoUtils.extractBirthDate(residentRegNo);

        return patientRepository.existsByResidentRegNo(
                residentRegNo
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientListResponseDto> getPatients() {
        return patientRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(PatientListResponseDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PatientValidationResponseDto validatePatient(
            UUID patientId
    ) {
        boolean valid =
                patientRepository.existsByPatientIdAndStatusCd(
                        patientId,
                        PatientStatus.ACTIVE
                );

        return new PatientValidationResponseDto(
                patientId,
                valid
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDetailResponseDto getPatient(UUID patientId) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(
                        () -> new BusinessException(
                                ErrorCode.PATIENT_NOT_FOUND
                        )
                );

        return PatientDetailResponseDto.from(patient);
    }
}
