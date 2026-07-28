package kr.co.seoulit.his.patientservice.patientRegister.mapper;

import kr.co.seoulit.his.patientservice.patientRegister.dto.PatientRegisterDto;
import kr.co.seoulit.his.patientservice.patientRegister.entity.PatientRegisterEntity;
import org.springframework.stereotype.Component;


/**
 * [Mapper] DTO ↔ Entity 변환
 * - toEntity: 등록 시 DTO → Entity
 */
@Component
public class PatientRegisterMapper {

    // ========== [등록용] DTO → Entity ==========
    public static PatientRegisterEntity toEntity(PatientRegisterDto dto) {
        PatientRegisterEntity entity = new PatientRegisterEntity();
        entity.setPatientName(dto.getPatientName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setResidentRegNo(dto.getResidentRegNo());
        entity.setStatusCd(dto.getStatusCd());
        return entity;
    }
}
