package gr.grodov.grsso.authorization_sso.security.utils;

import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ScopedClaimsBuilderTest {

    @Test
    void idTokenClaims_withCorrectData_returnIdTokenClaims() {
        var user = UserInfoDto.builder().id(1L).email("test@test.com").build();
        var scopes = Set.of("openid", "email");

        var result = ScopedClaimsBuilder.idTokenClaims(user, scopes);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo("1");
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void idTokenClaims_withUnknownScope_returnIdTokenClaimsWithoutUnknownScopes() {
        var user = UserInfoDto.builder().id(1L).email("test@test.com").build();
        var scopes = Set.of("openid", "email", "icon");

        var result = ScopedClaimsBuilder.idTokenClaims(user, scopes);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo("1");
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void accessTokenClaims_withCorrectData_returnAlwaysClaimsSub() {
        var user = UserInfoDto.builder().id(1L).email("test@test.com").build();

        var result = ScopedClaimsBuilder.accessTokenClaims(user);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo("1");
        assertThat(result.size()).isEqualTo(1);
    }
}