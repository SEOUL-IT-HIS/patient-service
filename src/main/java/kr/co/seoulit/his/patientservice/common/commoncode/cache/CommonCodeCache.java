package kr.co.seoulit.his.patientservice.common.commoncode.cache;

import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class CommonCodeCache {

    private volatile Set<String> genderCodes = Set.of();

    /**
     * 기존 성별 코드 캐시를 새로운 값으로 교체한다.
     */
    public void replaceGenderCodes(Set<String> genderCodes) {
        this.genderCodes = Set.copyOf(genderCodes);
    }

    /**
     * 전달받은 값이 유효한 성별 코드인지 확인한다.
     */
    public boolean isValidGenderCode(String genderCode) {
        return genderCodes.contains(genderCode);
    }

    /**
     * 현재 저장된 성별 코드를 반환한다.
     */
    public Set<String> getGenderCodes() {
        return genderCodes;
    }
}