package kr.co.seoulit.his.patientservice.common.commoncode.client;

import java.util.List;

import kr.co.seoulit.his.patientservice.common.commoncode.dto.AdminApiResponse;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeGroupResponse;
import kr.co.seoulit.his.patientservice.common.commoncode.dto.CommonCodeItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AdminCommonCodeClient {

    private final RestClient restClient;

    public AdminCommonCodeClient(
            @Value("${admin-service.base-url}") String adminServiceBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(adminServiceBaseUrl)
                .build();
    }

    /**
     * admin-service에서 공통코드 그룹 전체 목록을 조회한다.
     */
    public List<CommonCodeGroupResponse> getGroups() {
        AdminApiResponse<List<CommonCodeGroupResponse>> response =
                restClient
                        .get()
                        .uri("/api/commonCodeGroup/list")
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.data() == null) {
            throw new IllegalStateException("admin-service 공통코드 그룹 응답이 없습니다.");
        }

        return response.data();
    }

    /**
     * groupId에 해당하는 공통코드 항목 목록을 조회한다.
     */
    public List<CommonCodeItemResponse> getItems(String groupId) {
        AdminApiResponse<List<CommonCodeItemResponse>> response =
                restClient
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/commonCodeItem/list")
                                .queryParam("groupId", groupId)
                                .build())
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.data() == null) {
            throw new IllegalStateException("admin-service 공통코드 항목 응답이 없습니다.");
        }

        return response.data();
    }
}
