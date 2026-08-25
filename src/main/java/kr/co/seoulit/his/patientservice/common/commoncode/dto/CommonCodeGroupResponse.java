package kr.co.seoulit.his.patientservice.common.commoncode.dto;

public record CommonCodeGroupResponse(
        String groupId,
        String groupCode,
        String groupName,
        String useYn
) {
}