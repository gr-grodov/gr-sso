package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.oauth_client.domain.repo.OAuthClientRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomRegisteredClientRepositoryTest {

    @Mock
    private OAuthClientRepo clientRepo;
    @Mock
    private Mapper<OAuthClient, RegisteredClient> clientMapper;
    @InjectMocks
    private CustomRegisteredClientRepository registeredClientRepository;

    @Test
    void save_withCorrectData_callSaveInDB() {
        var client = buildRegisteredClient();

        registeredClientRepository.save(client);

        verify(clientRepo).save(any());
    }

    @Test
    void findById_withExistClient_returnRegisteredClient() {
        var oauthClient = buildOAuthClient();
        var client = buildRegisteredClient();
        when(clientRepo.findByIdAndStatus("1", OAuthClientStatus.ACTIVE)).thenReturn(Optional.of(oauthClient));
        when(clientMapper.fromDB(oauthClient)).thenReturn(client);

        var result = registeredClientRepository.findById("1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getClientId()).isEqualTo("crm-client");
    }

    @Test
    void findById_withNoExistClient_returnNull() {
        when(clientRepo.findByIdAndStatus("1", OAuthClientStatus.ACTIVE)).thenReturn(Optional.empty());

        var result = registeredClientRepository.findById("1");

        assertThat(result).isNull();
    }

    @Test
    void findByClientId_withExistClient_returnRegisteredClient() {
        var oauthClient = buildOAuthClient();
        var client = buildRegisteredClient();
        when(clientRepo.findByClientIdAndStatus("crm-client", OAuthClientStatus.ACTIVE)).thenReturn(Optional.of(oauthClient));
        when(clientMapper.fromDB(oauthClient)).thenReturn(client);

        var result = registeredClientRepository.findByClientId("crm-client");

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("crm-client");
    }

    @Test
    void findByClientId_withNoExistClient_returnNull() {
        when(clientRepo.findByClientIdAndStatus("crm-client", OAuthClientStatus.ACTIVE)).thenReturn(Optional.empty());

        var result = registeredClientRepository.findByClientId("crm-client");

        assertThat(result).isNull();
    }

    private RegisteredClient buildRegisteredClient() {
        RegisteredClient.Builder builder = RegisteredClient.withId("1")
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

        return builder.build();
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
}