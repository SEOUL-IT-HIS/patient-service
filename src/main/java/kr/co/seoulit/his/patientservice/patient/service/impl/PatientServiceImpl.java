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

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientEntity createPatient(
            PatientDto dto
    ) {
        if (patientRepository.existsByResidentRegNo(
                dto.getResidentRegNo()
        )) {
            throw new IllegalArgumentException(
                    "이미 등록된 주민등록번호입니다."
            );
        }

        PatientEntity entity = PatientMapper.toEntity(dto);

        return patientRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResidentRegNoDuplicate(String residentRegNo) {
        return patientRepository.existsByResidentRegNo(
                residentRegNo
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientListResponseDto> getPatients() {
        return patientRepository
                .findAll(Sort.by(Sort.Direction.DESC, "patientId"))
                .stream()
                .map(PatientListResponseDto::from)
                .toList();
    }
}
