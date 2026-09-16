package kr.co.seoulit.his.patientservice.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

@Configuration
public class RedisSessionConfig {
    private static final String ALLOWED_PACKAGE = "kr.co.seoulit.his.";

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        PolymorphicTypeValidator allowedTypes = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ALLOWED_PACKAGE)
                .build();

        return GenericJacksonJsonRedisSerializer.builder()
                .customize(builder -> builder.activateDefaultTyping(
                        allowedTypes, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY))
                .build();
    }
}
