package gr.grodov.grsso.oauth_session.service;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_session.service.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.domain.entity.DeviceType;
import gr.grodov.grsso.oauth_session.exception.OAuth2SessionNotFoundException;
import gr.grodov.grsso.oauth_session.service.dto.DeviceContext;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import gr.grodov.grsso.oauth_session.domain.entity.OAuth2Session;
import gr.grodov.grsso.oauth_session.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.oauth_session.exception.ErrorCreateOAuth2SessionException;
import gr.grodov.grsso.oauth_session.service.dto.GeoLocation;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SessionServiceTest {

    private final static UUID SID = UUID.randomUUID();
    private final static UUID USER_ID = UUID.randomUUID();

    @Mock
    private OAuth2SessionRepo sessionRepo;
    @Autowired
    @Mock
    private Mapper<OAuth2Session, OAuth2SessionDto> sessionMapper;
    @Mock
    private GeoLocationResolverService geoLocationResolver;
    @Mock
    private OAuth2AuthorizationService authorizationService;
    @Mock
    private RegisteredClientRepository registeredClientRepository;
    @InjectMocks
    private OAuth2SessionService sessionService;

    @Test
    void list_returnSessions() {
        var session = buildOAuth2Session();
        var sessionDto = OAuth2SessionDto.builder().sid(SID).build();
        when(sessionRepo.findAllByUserId(USER_ID)).thenReturn(List.of(session));
        when(sessionMapper.fromDB(session)).thenReturn(sessionDto);

        var result = sessionService.list(USER_ID.toString(), "device-id");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().sid()).isEqualTo(SID);
    }

    @Test
    void createOrUpdateSession_withValidClient_createSession() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var deviceContext = buildDeviceContext();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId(USER_ID, "crm-id-1", "device-id")).thenReturn(Optional.empty());
        when(registeredClientRepository.findById("crm-id-1")).thenReturn(client);
        when(geoLocationResolver.resolve("127.0.0.1")).thenReturn(new GeoLocation("COUNTRY", "CITY"));

        ArgumentCaptor<OAuth2Session> session = ArgumentCaptor.forClass(OAuth2Session.class);
        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).save(session.capture());
        assertThat(session.getValue()).isNotNull().satisfies(oAuth2Session -> {
            assertThat(oAuth2Session.getAuthorizationId()).isEqualTo("auth-123");
            assertThat(oAuth2Session.getUserId()).isEqualTo(USER_ID);
            assertThat(oAuth2Session.getClientId()).isEqualTo("crm-id-1");
            assertThat(oAuth2Session.getClientName()).isEqualTo("CRM Client");
            assertThat(oAuth2Session.getDeviceId()).isEqualTo("device-id");
            assertThat(oAuth2Session.getDeviceIpAddress()).isEqualTo("127.0.0.1");
            assertThat(oAuth2Session.getDeviceLocationCountry()).isEqualTo("COUNTRY");
            assertThat(oAuth2Session.getDeviceLocationCity()).isEqualTo("CITY");
            assertThat(oAuth2Session.getDeviceUserAgent()).isEqualTo("Desktop: Linux (Chrome)");
            assertThat(oAuth2Session.getDeviceType()).isEqualTo(DeviceType.DESKTOP);
        });
    }

    @Test
    void createOrUpdateSession_withInvalidClient_throwsErrorCreateOAuth2SessionException() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var deviceContext = buildDeviceContext();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId(USER_ID, "crm-id-1", "device-id")).thenReturn(Optional.empty());
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
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId(USER_ID, "crm-id-1", "device-id")).thenReturn(Optional.of(session));
        when(geoLocationResolver.resolve("127.0.0.1")).thenReturn(new GeoLocation("COUNTRY", "CITY"));

        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).saveAndFlush(any());
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void createOrUpdateSession_withDifferentAuthorization_updateSessionAndRemovePrevAuthorization() {
        var client = buildRegisteredClient();
        var authorization = OAuth2Authorization.from(buildAuthorization(client)).id("auth-321").build();
        var deviceContext = buildDeviceContext();
        var session = buildOAuth2Session();
        when(sessionRepo.findByUserIdAndClientIdAndDeviceId(USER_ID, "crm-id-1", "device-id")).thenReturn(Optional.of(session));
        when(authorizationService.findById("auth-123")).thenReturn(buildAuthorization(client));
        when(geoLocationResolver.resolve("127.0.0.1")).thenReturn(new GeoLocation("COUNTRY", "CITY"));

        sessionService.createOrUpdateSession(authorization, deviceContext);

        verify(sessionRepo).saveAndFlush(any());
        verify(authorizationService).remove(any());
    }

    @Test
    void updateSession_withExistSession_callUpdateSession() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var session = buildOAuth2Session();
        when(sessionRepo.findByAuthorizationId("auth-123")).thenReturn(Optional.of(session));

        sessionService.updateSession(authorization);

        verify(sessionRepo).updateAuthorization(eq(SID), any(), eq("auth-123"));
    }

    @Test
    void updateSession_withNoExistSession_throwsOAuth2SessionNotFoundException() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        when(sessionRepo.findByAuthorizationId("auth-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.updateSession(authorization))
            .isExactlyInstanceOf(OAuth2SessionNotFoundException.class)
            .satisfies((ex) -> {
                var exception = (OAuth2SessionNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth2_session_not_found");
            });
    }

    @Test
    void getSID_withExistSession_returnSID() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        var session = buildOAuth2Session();
        when(sessionRepo.findByAuthorizationId("auth-123")).thenReturn(Optional.of(session));

        var result = sessionService.getSID(authorization);

        assertThat(result).isEqualTo(SID.toString());
    }

    @Test
    void getSID_withNoExistSession_throwsOAuth2SessionNotFoundException() {
        var client = buildRegisteredClient();
        var authorization = buildAuthorization(client);
        when(sessionRepo.findByAuthorizationId("auth-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSID(authorization))
            .isExactlyInstanceOf(OAuth2SessionNotFoundException.class)
            .satisfies((ex) -> {
                var exception = (OAuth2SessionNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth2_session_not_found");
            });
    }

    @Test
    void getSessionBySID_withExistSession_returnSession() {
        var session = buildOAuth2Session();
        var sessionDto = OAuth2SessionDto.builder().sid(SID).build();
        when(sessionMapper.fromDB(session)).thenReturn(sessionDto);
        when(sessionRepo.findBySidAndUserId(SID, USER_ID)).thenReturn(Optional.of(session));

        var result = sessionService.getSessionBySID(SID.toString(), USER_ID);

        assertThat(result).isExactlyInstanceOf(OAuth2SessionDto.class);
        assertThat(result.sid()).isEqualTo(SID);
    }

    @Test
    void getSessionBySID_withNoExistSession_throwsOAuth2SessionNotFoundException() {
        when(sessionRepo.findBySidAndUserId(SID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.getSessionBySID(SID.toString(), USER_ID))
            .isExactlyInstanceOf(OAuth2SessionNotFoundException.class)
            .satisfies((ex) -> {
                var exception = (OAuth2SessionNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("oauth2_session_not_found");
            });
    }

    @Test
    void deleteSession_callDelete() {
        sessionService.deleteSession(SID.toString());

        verify(sessionRepo).deleteById(SID);
    }

    private DeviceContext buildDeviceContext() {
        return new DeviceContext("device-id", "127.0.0.1", "Desktop: Linux (Chrome)", DeviceType.DESKTOP);
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
            .principalName(USER_ID.toString())
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .build();
    }

    private OAuth2Session buildOAuth2Session() {
        return OAuth2Session.builder()
            .sid(SID)
            .authorizationId("auth-123")
            .userId(USER_ID)
            .clientId("crm-id-1")
            .clientName("CRM Client")
            .deviceId("device-id")
            .deviceIpAddress("127.0.0.1")
            .deviceUserAgent("Desktop: Linux (Browser, Chrome)")
        .build();
    }
}