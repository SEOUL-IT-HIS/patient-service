package kr.co.seoulit.his.patientservice.patient.service;

import java.util.HashMap;
import java.util.Map;
import kr.co.seoulit.his.patientservice.patient.mapper.PatientProcedureMapper;
import kr.co.seoulit.his.patientservice.patient.type.PatientStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientProcedureService {
    private final PatientProcedureMapper mapper;

    public Map<String, Object> countPatients(PatientStatus statusCd) {
        Map<String, Object> param = new HashMap<>();
        param.put("statusCd", statusCd.name());
        // 프로시저 실행 후 OUT 값이 같은 Map의 count에 저장됩니다.
        mapper.countPatients(param);
        return param;
    }
}
