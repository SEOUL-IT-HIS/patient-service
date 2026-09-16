package kr.co.seoulit.his.patientservice;

import kr.co.seoulit.his.common.session.SessionUser;
import kr.co.seoulit.his.patientservice.common.config.RedisSessionConfig;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class RedisSessionSerializationTests {
    @Test
    void readsSharedAdminUserJson() {
        RedisSerializer<Object> serializer = new RedisSessionConfig().springSessionDefaultRedisSerializer();
        String json = """
                {"@class":"kr.co.seoulit.his.common.session.SessionUser",
                 "accountId":"1","accountStatus":"01","empId":"2",
                 "empNo":"E202608001","empName":"tester","deptCode":"01",
                 "loginId":"tester","roleCodes":"01,02","menuCodes":"01,02"}
                """;
        Object value = serializer.deserialize(json.getBytes(StandardCharsets.UTF_8));
        assertThat(value).isInstanceOf(SessionUser.class);
        SessionUser user = (SessionUser) value;
        assertThat(user.getRoleCodes()).isEqualTo("01,02");
        assertThat(user.getEmpNo()).isEqualTo("E202608001");
        assertThat(serializer.deserialize(serializer.serialize(user))).isEqualTo(user);
    }
}
