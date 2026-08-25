package kr.co.seoulit.his.patientservice.common.commoncode.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.seoulit.his.patientservice.common.commoncode.cache.CommonCodeCache;
import kr.co.seoulit.his.patientservice.common.commoncode.client.AdminCommonCodeClient;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonCodeCacheService {

  private static final String GENDER_GROUP_CODE = "GENDER_CD";

  private final AdminCommonCodeClient adminCommonCodeClient;
  private final CommonCodeCache commonCodeCache;

  public void loadGenderCodes() {
    CommonCodeGroupResponse genderGroup =
        adminCommonCodeClient.getGroups().stream()
            .filter(group -> GENDER_GROUP_CODE.equals(group.groupCode()))
            .filter(group -> "Y".equals(group.useYn()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalStateException("GENDER_CD 공통코드 그룹을 찾을 수 없습니다."));

    List<CommonCodeItemResponse> items =
        adminCommonCodeClient.getItems(genderGroup.groupId());

    Set<String> genderCodes =
        items.stream()
            .filter(item -> "Y".equals(item.useYn()))
            .map(CommonCodeItemResponse::codeValue)
            .collect(Collectors.toUnmodifiableSet());

    if (genderCodes.isEmpty()) {
      throw new IllegalStateException("사용 가능한 성별 공통코드가 없습니다.");
    }

    commonCodeCache.replaceGenderCodes(genderCodes);
  }
}
