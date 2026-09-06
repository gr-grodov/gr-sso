package gr.grodov.grsso.session_sso.service;

import gr.grodov.grsso.session_sso.service.dto.DeviceContext;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import gr.grodov.grsso.session_sso.domain.entity.OAuth2Session;
import gr.grodov.grsso.session_sso.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.session_sso.exception.ErrorCreateOAuth2SessionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SessionServiceTest {

    private final static UUID SID = UUID.randomUUID();

    @Mock
    private OAuth2SessionRepo sessionRepo;
    @Autowired
    @Mock
    private OAuth2AuthorizationService authorizationService;
    @Mock
    private RegisteredClientRepository registeredClientRepository;
    @InjectMocks
    private OAuth2SessionService sessionService;

    @Test
    void createOrUpdateSession_withValidClient_createSession() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var deviceContext = buildDeviceContext();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId("1", "crm-id-1", "device-id")).thenReturn(Optional.empty());
        when(registeredClientRepository.findById("crm-id-1")).thenReturn(client);

        ArgumentCaptor<OAuth2Session> session = ArgumentCaptor.forClass(OAuth2Session.class);
        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).save(session.capture());
        assertThat(session.getValue()).isNotNull().satisfies(oAuth2Session -> {
            assertThat(oAuth2Session.getAuthorizationId()).isEqualTo("auth-123");
            assertThat(oAuth2Session.getUserId()).isEqualTo(1);
            assertThat(oAuth2Session.getClientId()).isEqualTo("crm-id-1");
            assertThat(oAuth2Session.getClientName()).isEqualTo("CRM Client");
            assertThat(oAuth2Session.getDeviceId()).isEqualTo("device-id");
            assertThat(oAuth2Session.getDeviceIpAddress()).isEqualTo("127.0.0.1");
            assertThat(oAuth2Session.getDeviceUserAgent()).isEqualTo("Desktop: Linux (Browser, Chrome)");
        });
    }

    @Test
    void createOrUpdateSession_withInvalidClient_throwsErrorCreateOAuth2SessionException() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var deviceContext = buildDeviceContext();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId("1", "crm-id-1", "device-id")).thenReturn(Optional.empty());
        when(registeredClientRepository.findById("crm-id-1")).thenReturn(null);

        assertThatThrownBy(() -> sessionService.createOrUpdateSession(authorization, deviceContext))
            .isInstanceOf(ErrorCreateOAuth2SessionException.class)
            .satisfies(ex -> {
                var exception = (ErrorCreateOAuth2SessionException) ex;
                assertThat(exception.getCode()).isEqualTo("error_create_oauth2_session");
            });

        verify(sessionRepo, never()).save(any());
    }

    @Test
    void createOrUpdateSession_withEqualsAuthorization_updateSession() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var deviceContext = buildDeviceContext();
        var session = buildOAuth2Session();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId("1", "crm-id-1", "device-id")).thenReturn(Optional.of(session));

        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).updateAuthorization(SID, "auth-123");
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void createOrUpdateSession_withDifferentAuthorization_updateSessionAndRemovePrevAuthorization() {
        var client = buildRegisteredClient();
        var authorization = OAuth2Authorization.from(buildAuthorization(client)).id("auth-321").build();
        var deviceContext = buildDeviceContext();
        var session = buildOAuth2Session();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId("1", "crm-id-1", "device-id")).thenReturn(Optional.of(session));
        when(authorizationService.findById("auth-123")).thenReturn(buildAuthorization(client));

        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).updateAuthorization(SID, "auth-321");
        verify(authorizationService).remove(any());
    }

    private DeviceContext buildDeviceContext() {
        return new DeviceContext("device-id", "127.0.0.1", "Desktop: Linux (Chrome)");
    }

    private RegisteredClient buildRegisteredClient() {
        RegisteredClient.Builder builder = RegisteredClient.withId("crm-id-1")
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

    private OAuth2Authorization buildAuthorization(RegisteredClient client) {
        return OAuth2Authorization.withRegisteredClient(client)
            .id("auth-123")
            .principalName("1")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .build();
    }

    private OAuth2Session buildOAuth2Session() {
        return OAuth2Session.builder()
            .sid(SID)
            .authorizationId("auth-123")
            .userId(1L)
            .clientId("crm-id-1")
            .clientName("CRM Client")
            .deviceId("device-id")
            .deviceIpAddress("127.0.0.1")
            .deviceUserAgent("Desktop: Linux (Browser, Chrome)")
        .build();
    }
}