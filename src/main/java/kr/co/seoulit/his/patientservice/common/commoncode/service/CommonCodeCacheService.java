package kr.co.seoulit.his.patientservice.common.commoncode.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kr.co.seoulit.his.patientservice.common.commoncode.cache.CommonCodeCache;
import kr.co.seoulit.his.patientservice.common.commoncode.client.AdminCommonCodeClient;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeItemResponse;
import kr.co.seoulit.his.patientservice.common.commoncode.model.CachedCommonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonCodeCacheService {

    private final AdminCommonCodeClient adminCommonCodeClient;
    private final CommonCodeCache commonCodeCache;

    /**
     * admin-service의 모든 활성 공통코드를 조회해 로컬 캐시에 적재한다.
     */
    public void loadAllCommonCodes() {
        List<CommonCodeGroupResponse> activeGroups =
                adminCommonCodeClient
                        .getGroups()
                        .stream()
                        .filter(group -> "Y".equals(group.useYn()))
                        .toList();

        Map<String, Map<String, CachedCommonCode>> allCodes =
                new LinkedHashMap<>();

        for (CommonCodeGroupResponse group : activeGroups) {
            List<CommonCodeItemResponse> items =
                    adminCommonCodeClient.getItems(group.groupId());

            Map<String, CachedCommonCode> activeItems =
                    new LinkedHashMap<>();

            for (CommonCodeItemResponse item : items) {
                if (!"Y".equals(item.useYn())) {
                    continue;
                }

                CachedCommonCode cachedCode =
                        new CachedCommonCode(
                                item.codeValue(),
                                item.codeName()
                        );

                activeItems.put(item.codeValue(), cachedCode);
            }

            allCodes.put(group.groupCode(), activeItems);
        }

        commonCodeCache.replaceAll(allCodes);
    }
}