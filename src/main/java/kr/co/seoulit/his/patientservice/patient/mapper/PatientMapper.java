package kr.co.seoulit.his.patientservice.patient.mapper;

import kr.co.seoulit.his.patientservice.patient.dto.PatientDto;
import kr.co.seoulit.his.patientservice.patient.entity.PatientEntity;
import org.springframework.stereotype.Component;


/**
 * [Mapper] DTO ↔ Entity 변환
 * - toEntity: 등록 시 DTO → Entity
 */
@Component
public class PatientMapper {

    public static PatientEntity toEntity(PatientDto dto) {
        PatientEntity entity = new PatientEntity();
        entity.setPatientName(dto.getPatientName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setResidentRegNo(dto.getResidentRegNo());
        entity.setGenderCd(dto.getGenderCd());
        entity.setStatusCd(dto.getStatusCd());
        entity.setTempPatientYn(dto.getTempPatientYn());
        return entity;
    }
}
