package kr.co.seoulit.his.patientservice.patientRegister.service.impl;

import kr.co.seoulit.his.patientservice.patientRegister.dto.PatientRegisterDto;
import kr.co.seoulit.his.patientservice.patientRegister.entity.PatientRegisterEntity;
import kr.co.seoulit.his.patientservice.patientRegister.mapper.PatientRegisterMapper;
import kr.co.seoulit.his.patientservice.patientRegister.repository.PatientRegisterRepository;
import kr.co.seoulit.his.patientservice.patientRegister.service.PatientRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional

public class PatientRegisterServiceImpl implements PatientRegisterService {

    private final PatientRegisterRepository patientRegisterRepository;

    @Override
    public PatientRegisterEntity insertPatientRegister(
            PatientRegisterDto dto
    ) {
        if (patientRegisterRepository.existsByResidentRegNo(
                dto.getResidentRegNo()
        )) {
            throw new IllegalArgumentException(
                    "이미 등록된 주민등록번호입니다."
            );
        }

        PatientRegisterEntity entity =
                PatientRegisterMapper.toEntity(dto);

        return patientRegisterRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkDuplicateResidentRegNo(String residentRegNo) {
        return patientRegisterRepository.existsByResidentRegNo(
                residentRegNo
        );
    }

}


