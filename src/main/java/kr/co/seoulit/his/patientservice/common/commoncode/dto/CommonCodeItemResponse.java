package kr.co.seoulit.his.patientservice.common.commoncode.dto;

public record CommonCodeItemResponse(
        String codeId,
        String groupId,
        String codeValue,
        String codeName,
        String useYn
) {
}