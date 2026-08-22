package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.oauth_client.domain.mapper.RegisteredClientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class RegisteredClientMapperTest {

    private final RegisteredClientMapper mapper = new RegisteredClientMapper();

    @Test
    void fromDB_withCorrectData_successMapper() {
        OAuthClient client = buildOAuthClient();

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getId()).isEqualTo(client.getId());
        assertThat(result.getClientId()).isEqualTo(client.getClientId());
        assertThat(result.getClientIdIssuedAt()).isEqualTo(client.getClientIdIssuedAt());
        assertThat(result.getClientSecret()).isEqualTo(client.getClientSecret());
        assertThat(result.getClientSecretExpiresAt()).isEqualTo(client.getClientSecretExpiresAt());
        assertThat(result.getClientName()).isEqualTo(client.getClientName());
    }

    @Test
    void fromDB_withRedirectUris_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.setRedirectUris(Set.of("http://localhost:8080/login/oauth2/code/grsso"));
        client.setPostLogoutRedirectUris(Set.of("http://localhost:8080/logout-success"));

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getRedirectUris()).containsExactly("http://localhost:8080/login/oauth2/code/grsso");
        assertThat(result.getPostLogoutRedirectUris()).containsExactly("http://localhost:8080/logout-success");
    }

    @Test
    void fromDB_withScopes_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.setScopes(Set.of(OAuthScope.OPEN_ID, OAuthScope.PROFILE));

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getScopes()).containsExactlyInAnyOrder(OAuthScope.OPEN_ID.getScopeValue(), OAuthScope.PROFILE.getScopeValue());
    }

    @ParameterizedTest
    @EnumSource(OAuthAuthorizationGrantType.class)
    void fromDB_withGrantTypes_successMapper(OAuthAuthorizationGrantType dbType) {
        OAuthClient client = buildOAuthClient();
        client.setAuthorizationGrantTypes(Set.of(dbType));

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getAuthorizationGrantTypes())
            .extracting(AuthorizationGrantType::getValue)
            .containsExactly(dbType.getValue());
    }

    @ParameterizedTest
    @EnumSource(OAuthClientAuthenticationMethod.class)
    void fromDB_withAuthenticationMethods_successMapper(OAuthClientAuthenticationMethod dbMethod) {
        OAuthClient client = buildOAuthClient();
        client.setClientAuthenticationMethods(Set.of(dbMethod));

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getClientAuthenticationMethods())
            .extracting(ClientAuthenticationMethod::getValue)
            .containsExactly(dbMethod.getValue());
    }

    @Test
    void fromDB_withClientSettings_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.getClientSettings().setRequireAuthorizationConsent(true);
        client.getClientSettings().setRequireProofKey(true);

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getClientSettings().isRequireAuthorizationConsent()).isTrue();
        assertThat(result.getClientSettings().isRequireProofKey()).isTrue();
    }

    @Test
    void fromDB_withClientSettingsWithNullableJwkSetUrl_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.getClientSettings().setJwkSetUrl(null);

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getClientSettings().getJwkSetUrl()).isNull();
    }

    @Test
    void fromDB_withClientSettingsWithNullableSigningAlgorithm_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.getClientSettings().setTokenEndpointAuthenticationSigningAlgorithm(null);

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getClientSettings().getTokenEndpointAuthenticationSigningAlgorithm()).isNull();
    }


    @Test
    void fromDB_withTokenSettings_successMapper() {
        OAuthClient client = buildOAuthClient();
        client.getTokenSettings().setAccessTokenTimeToLive(300L);
        client.getTokenSettings().setRefreshTokenTimeToLive(2_592_000L);
        client.getTokenSettings().setAuthorizationCodeTimeToLive(300L);
        client.getTokenSettings().setReuseRefreshTokens(false);

        RegisteredClient result = mapper.fromDB(client);

        assertThat(result.getTokenSettings().getAccessTokenTimeToLive()).isEqualTo(Duration.ofMinutes(5));
        assertThat(result.getTokenSettings().getRefreshTokenTimeToLive()).isEqualTo(Duration.ofDays(30));
        assertThat(result.getTokenSettings().getAuthorizationCodeTimeToLive()).isEqualTo(Duration.ofMinutes(5));
        assertThat(result.getTokenSettings().isReuseRefreshTokens()).isFalse();
    }


    @Test
    void toDB_withCorrectData_successMapper() {
        RegisteredClient client = buildRegisteredClient();

        OAuthClient result = mapper.toDB(client);

        assertThat(result.getId()).isEqualTo(client.getId());
        assertThat(result.getClientId()).isEqualTo(client.getClientId());
        assertThat(result.getClientSecret()).isEqualTo(client.getClientSecret());
        assertThat(result.getClientName()).isEqualTo(client.getClientName());
    }


    @ParameterizedTest
    @EnumSource(OAuthAuthorizationGrantType.class)
    void toDB_withGrantTypes_successMapper(OAuthAuthorizationGrantType dbType) {
        RegisteredClient client = buildRegisteredClient(builder -> builder
            .authorizationGrantTypes(types -> {
                types.clear();
                types.add(new AuthorizationGrantType(dbType.getValue()));
            })
        );

        OAuthClient result = mapper.toDB(client);

        assertThat(result.getAuthorizationGrantTypes()).containsExactly(dbType);
    }

    private OAuthClient buildOAuthClient() {
        return OAuthClient.builder()
            .id(UUID.randomUUID().toString())
            .clientId("crm-client")
            .clientIdIssuedAt(Instant.now())
            .clientSecret("encoded-secret")
            .clientSecretExpiresAt(null)
            .clientName("CRM Client")
            .clientAuthenticationMethods(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC))
            .authorizationGrantTypes(Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE))
            .redirectUris(Set.of("http://localhost:8080/login/oauth2/code/grsso"))
            .postLogoutRedirectUris(Set.of())
            .scopes(Set.of(OAuthScope.OPEN_ID))
            .clientSettings(OAuthClientSettings.builder()
                .requireAuthorizationConsent(false)
                .requireProofKey(false)
            .build())
            .tokenSettings(OAuthTokenSettings.builder()
                .authorizationCodeTimeToLive(300L)
                .accessTokenTimeToLive(300L)
                .refreshTokenTimeToLive(2_592_000L)
                .reuseRefreshTokens(false)
            .build())
        .build();
    }

    private RegisteredClient buildRegisteredClient() {
        return buildRegisteredClient(builder -> {});
    }

    private RegisteredClient buildRegisteredClient(Consumer<RegisteredClient.Builder> customizer) {
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("crm-client")
            .clientIdIssuedAt(Instant.now())
            .clientSecret("encoded-secret")
            .clientName("CRM Client")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/login/oauth2/code/grsso")
            .scope(OAuthScope.OPEN_ID.getScopeValue())
            .clientSettings(ClientSettings.builder().build())
            .tokenSettings(TokenSettings.builder()
                .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                .accessTokenTimeToLive(Duration.ofMinutes(5))
                .refreshTokenTimeToLive(Duration.ofDays(30))
            .build());

        customizer.accept(builder);
        return builder.build();
    }
}