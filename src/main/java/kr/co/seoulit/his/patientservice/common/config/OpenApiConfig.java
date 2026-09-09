package kr.co.seoulit.his.patientservice.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI patientServiceOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Patient Service API")
                                .description("환자 기본정보·주소·연락처, 환자 안전정보 및 통계 API. 안전정보는 UTF-8 기준 2000바이트 제한. 통계 API 이외의 성공 응답은 code/message/data 형식입니다.")
                                .version("v1"));
    }
}
