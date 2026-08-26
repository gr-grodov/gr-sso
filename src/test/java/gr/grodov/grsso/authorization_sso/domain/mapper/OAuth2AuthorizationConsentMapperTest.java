package gr.grodov.grsso.authorization_sso.domain.mapper;

import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsent;
import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsentId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2AuthorizationConsentMapperTest {

    private final OAuth2AuthorizationConsentMapper mapper = new OAuth2AuthorizationConsentMapper();

    @Test
    void fromDB_withCorrectData_successMapper() {
        OAuthConsent consent = buildOAuthConsent();

        OAuth2AuthorizationConsent result = mapper.fromDB(consent);

        assertThat(result.getRegisteredClientId()).isEqualTo(consent.getId().getRegisteredClientId());
        assertThat(result.getPrincipalName()).isEqualTo(consent.getId().getPrincipalName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"SCOPE_openid", "SCOPE_profile"})
    void fromDB_withScopes_successMapper(String scope) {
        OAuthConsent consent = buildOAuthConsent();
        consent.setAuthorities(Set.of(scope));

        OAuth2AuthorizationConsent result = mapper.fromDB(consent);

        assertThat(result.getAuthorities())
            .extracting(GrantedAuthority::getAuthority)
            .containsExactly(scope);
    }

    @Test
    void toDB_withCorrectData_successMapper() {
        OAuth2AuthorizationConsent consent = buildOAuth2AuthorizationConsent();

        OAuthConsent result = mapper.toDB(consent);

        assertThat(result.getId().getRegisteredClientId()).isEqualTo(consent.getRegisteredClientId());
        assertThat(result.getId().getPrincipalName()).isEqualTo(consent.getPrincipalName());
    }

    private OAuth2AuthorizationConsent buildOAuth2AuthorizationConsent() {
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
            "crm-client-id",
            "id"
        );

        Set.of("SCOPE_openid", "SCOPE_profile").forEach(auth ->
            builder.authority(new SimpleGrantedAuthority(auth))
        );

        return builder.build();
    }

    private OAuthConsent buildOAuthConsent() {
        return OAuthConsent.builder()
            .id(new OAuthConsentId("crm-client-id", "id"))
            .authorities(Set.of("SCOPE_openid", "SCOPE_profile"))
            .build();
    }
}