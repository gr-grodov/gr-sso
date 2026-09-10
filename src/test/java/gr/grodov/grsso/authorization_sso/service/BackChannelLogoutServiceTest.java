package gr.grodov.grsso.authorization_sso.service;

import gr.grodov.grsso.common.props.BackendAppProperties;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientSettings;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.oauth_session.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackChannelLogoutServiceTest {

    private final UUID SID = UUID.randomUUID();
    private final static UUID USER_ID = UUID.randomUUID();

    @Mock
    private Jwt jwtToken;
    @Mock
    private BackendAppProperties properties;
    @Mock
    private OAuth2SessionService sessionService;
    @Mock
    private OAuthClientsService clientsService;
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private BackChannelLogoutService logoutService;

    @Test
    void logoutFromClient_withCorrectSIDAndUserID_postLogoutTokenAndDeleteSession() {
        var session = buidlOAuth2SessionDto();
        var client = buildOAuthClientDto();
        when(sessionService.getSessionBySID(SID.toString(), USER_ID.toString())).thenReturn(session);
        when(clientsService.getById("client-id")).thenReturn(client);
        when(properties.backendUri()).thenReturn("http://gr-sso.com");
        when(jwtEncoder.encode(any())).thenReturn(jwtToken);
        when(jwtToken.getTokenValue()).thenReturn("logout_token");

        logoutService.logoutFromClient(SID.toString(), USER_ID.toString());

        verify(restTemplate).postForEntity(eq("https://example.com/logout/connect/back-channel/grsso"), any(), eq(Void.class));
        verify(sessionService).deleteSession(SID.toString());
    }

    @Test
    void logoutFromClient_errorPostLogoutToken_dontPostLogoutToken() {
        var session = buidlOAuth2SessionDto();
        var client = buildOAuthClientDto();
        when(sessionService.getSessionBySID(SID.toString(), USER_ID.toString())).thenReturn(session);
        when(clientsService.getById("client-id")).thenReturn(client);
        when(properties.backendUri()).thenReturn("http://gr-sso.com");
        when(jwtEncoder.encode(any())).thenReturn(jwtToken);
        when(jwtToken.getTokenValue()).thenReturn("logout_token");
        when(restTemplate.postForEntity(anyString(), any(), any())).thenThrow(RuntimeException.class);

        logoutService.logoutFromClient(SID.toString(), USER_ID.toString());

        verify(sessionService).deleteSession(SID.toString());
    }

    private OAuth2SessionDto buidlOAuth2SessionDto() {
        return OAuth2SessionDto.builder()
            .sid(SID)
            .userId(USER_ID)
            .clientId("client-id")
        .build();
    }

    private OAuthClientDto buildOAuthClientDto() {
        return OAuthClientDto.builder()
            .id("client-id")
            .clientId("client-123")
            .clientSettings(OAuthClientSettings.builder()
                .oidcLogoutRedirectUri("https://example.com/logout/connect/back-channel/grsso")
                .build()
            )
        .build();
    }
}