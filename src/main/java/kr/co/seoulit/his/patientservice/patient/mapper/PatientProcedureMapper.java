package kr.co.seoulit.his.patientservice.patient.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientProcedureMapper {
    void countPatients(Map<String, Object> param);
}
