package kr.co.seoulit.his.patientservice.patientsafety.mapper;

import kr.co.seoulit.his.patientservice.patientsafety.dto.PatientSafetyResponseDto;
import kr.co.seoulit.his.patientservice.patientsafety.entity.PatientSafetyEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientSafetyMapper {

    public PatientSafetyResponseDto toResponseDto(
            PatientSafetyEntity entity) {

        return new PatientSafetyResponseDto(
                entity.getSafetyInfoId(),
                entity.getPatientId(),
                entity.getSafetyNote(),
                entity.getActiveYn(),
                entity.getPinnedYn(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
