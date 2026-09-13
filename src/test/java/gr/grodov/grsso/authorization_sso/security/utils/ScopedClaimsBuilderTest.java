package gr.grodov.grsso.authorization_sso.security.utils;

import gr.grodov.grsso.user.service.dto.UserInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScopedClaimsBuilderTest {

    private final static UUID USER_ID = UUID.randomUUID();

    @Test
    void idTokenClaims_withCorrectData_returnIdTokenClaims() {
        var user = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();
        var scopes = Set.of("openid", "email");

        var result = ScopedClaimsBuilder.idTokenClaims(user, scopes);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void idTokenClaims_withUnknownScope_returnIdTokenClaimsWithoutUnknownScopes() {
        var user = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();
        var scopes = Set.of("openid", "email", "icon");

        var result = ScopedClaimsBuilder.idTokenClaims(user, scopes);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void accessTokenClaims_withCorrectData_returnAlwaysClaimsSub() {
        var user = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();

        var result = ScopedClaimsBuilder.accessTokenClaims(user);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.size()).isEqualTo(1);
    }
}