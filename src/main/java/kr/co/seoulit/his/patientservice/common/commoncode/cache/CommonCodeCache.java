package kr.co.seoulit.his.patientservice.common.commoncode.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import kr.co.seoulit.his.patientservice.common.commoncode.model.CachedCommonCode;
import org.springframework.stereotype.Component;

@Component
public class CommonCodeCache {

    private volatile Map<String, Map<String, CachedCommonCode>> codesByGroup =
            Map.of();

    /**
     * 전체 공통코드 캐시를 새로운 값으로 교체한다.
     */
    public void replaceAll(
            Map<String, Map<String, CachedCommonCode>> newCodesByGroup
    ) {
        Map<String, Map<String, CachedCommonCode>> copiedGroups =
                new HashMap<>();

        newCodesByGroup.forEach(
                (groupCode, items) ->
                        copiedGroups.put(groupCode, Map.copyOf(items))
        );

        this.codesByGroup = Map.copyOf(copiedGroups);
    }

    /**
     * 특정 그룹에 특정 코드가 존재하는지 확인한다.
     */
    public boolean contains(String groupCode, String codeValue) {
        if (groupCode == null || codeValue == null) {
            return false;
        }

        return codesByGroup
                .getOrDefault(groupCode, Map.of())
                .containsKey(codeValue);
    }

    /**
     * 특정 그룹의 특정 코드 정보를 조회한다.
     */
    public Optional<CachedCommonCode> find(
            String groupCode,
            String codeValue
    ) {
        if (groupCode == null || codeValue == null) {
            return Optional.empty();
        }

        CachedCommonCode code =
                codesByGroup
                        .getOrDefault(groupCode, Map.of())
                        .get(codeValue);

        return Optional.ofNullable(code);
    }

    /**
     * 특정 그룹의 모든 공통코드를 조회한다.
     */
    public Map<String, CachedCommonCode> getGroup(String groupCode) {
        if (groupCode == null) {
            return Map.of();
        }

        return codesByGroup.getOrDefault(groupCode, Map.of());
    }

    /**
     * 캐시에 저장된 그룹 코드 목록을 반환한다.
     */
    public Set<String> getGroupCodes() {
        return codesByGroup.keySet();
    }

    /**
     * 캐시에 저장된 그룹 개수를 반환한다.
     */
    public int getGroupCount() {
        return codesByGroup.size();
    }

    /**
     * 캐시에 저장된 전체 공통코드 항목 개수를 반환한다.
     */
    public int getItemCount() {
        return codesByGroup
                .values()
                .stream()
                .mapToInt(Map::size)
                .sum();
    }
}