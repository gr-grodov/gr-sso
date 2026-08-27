package gr.grodov.grsso.authentication.security.principal.jackson;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationJsonMapperTest {

    private JsonMapper authenticationMapper;
    private ObjectMapper jsonMapper;

    @BeforeEach
    void setUp() {
        this.authenticationMapper = new AuthenticationJsonMapper().getJsonMapper();
        this.jsonMapper = new ObjectMapper();
    }

    @Test
    void serialize_withUserInfo_returnCorrectData() {
        var userInfo = UserInfoDto.builder()
            .id(1L)
            .email("user@example.com")
            .password("Password123!")
            .enabled(true)
            .provider(AuthProvider.LOCAL)
            .role(Role.USER)
        .build();
        var principal = UserPrincipal.local(userInfo);

        String json = authenticationMapper.writeValueAsString(principal);
        JsonNode jsonNode = jsonMapper.readTree(json);

        assertThat(jsonNode.has("@class")).isTrue();
        assertThat(jsonNode.get("@class").stringValue()).isEqualTo(UserPrincipal.class.getName());
        assertThat(jsonNode.has("id")).isTrue();
        assertThat(jsonNode.get("id").asLong()).isEqualTo(1L);
        assertThat(jsonNode.has("email")).isTrue();
        assertThat(jsonNode.get("email").asString()).isEqualTo("user@example.com");
        assertThat(jsonNode.has("provider")).isTrue();
        assertThat(jsonNode.get("provider").asString()).isEqualTo("LOCAL");
        assertThat(jsonNode.has("authorities")).isTrue();
        assertThat(jsonNode.get("authorities").get(0).asString()).isEqualTo("USER");
        assertThat(jsonNode.has("password")).isFalse();
        assertThat(jsonNode.has("enabled")).isFalse();
    }

    @Test
    void deserialize_withJsonUserPrincipal_returnCorrectData() {
        String json = """
            {
                "@class":"gr.grodov.grsso.authentication.security.principal.UserPrincipal",
                "id":1,
                "email":"user@example.com",
                "provider":"LOCAL",
                "authorities":["USER"]
            }
        """;

        Object deserializeObject = authenticationMapper.readValue(json, Object.class);

        assertThat(deserializeObject).isExactlyInstanceOf(UserPrincipal.class);
        UserPrincipal userPrincipal = (UserPrincipal) deserializeObject;
        assertThat(userPrincipal.getId()).isEqualTo(1L);
        assertThat(userPrincipal.getEmail()).isEqualTo("user@example.com");
        assertThat(userPrincipal.getProvider()).isEqualTo(AuthProvider.LOCAL);
        assertThat(userPrincipal.getAuthorities()).isEqualTo(List.of(Role.USER));
    }
}