package kr.co.seoulit.his.patientservice.patientcontact.mapper;

import kr.co.seoulit.his.patientservice.patientcontact.dto.PatientContactResponseDto;
import kr.co.seoulit.his.patientservice.patientcontact.entity.PatientContactEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientContactMapper {

    public PatientContactResponseDto toResponseDto(
            PatientContactEntity entity) {

        return new PatientContactResponseDto(
                entity.getContactId(),
                entity.getPatientId(),
                entity.getZipCode(),
                entity.getAddress(),
                entity.getAddressDetail(),
                entity.getPhoneNo(),
                entity.getPrimaryYn(),
                entity.getActiveYn(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}