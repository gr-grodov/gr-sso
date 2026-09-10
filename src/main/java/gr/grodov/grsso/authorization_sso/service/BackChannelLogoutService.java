package gr.grodov.grsso.authorization_sso.service;

import gr.grodov.grsso.common.props.BackendAppProperties;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.oauth_session.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackChannelLogoutService {

    private static final String BACK_CHANNEL_LOGOUT_TOKEN_EVENT_NAME = "http://schemas.openid.net/event/backchannel-logout";

    private final BackendAppProperties properties;
    private final OAuth2SessionService sessionService;
    private final OAuthClientsService clientsService;
    private final JwtEncoder jwtEncoder;
    private final RestTemplate restTemplate;

    @Transactional
    public void logoutFromClient(String sid, String userId) {
        OAuth2SessionDto session = sessionService.getSessionBySID(sid, userId);
        OAuthClientDto client = clientsService.getById(session.clientId());

        String logoutToken = buildLogoutToken(session, client);
        String logoutURI = client.clientSettings().getOidcLogoutRedirectUri();
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("logout_token", logoutToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
            restTemplate.postForEntity(logoutURI, request, Void.class);
        } catch (Exception ex) {
            log.warn("Failed to send back-channel logout to {}: {}", logoutURI, ex.getMessage());
        } finally {
            sessionService.deleteSession(sid);
        }
    }

    private String buildLogoutToken(OAuth2SessionDto session, OAuthClientDto client) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .id(UUID.randomUUID().toString())
            .issuer(properties.backendUri())
            .issuedAt(now)
            .subject(session.userId().toString())
            .audience(List.of(client.clientId()))
            .claim(LogoutTokenClaimNames.SID, session.sid())
            .claim(LogoutTokenClaimNames.EVENTS, Map.of(BACK_CHANNEL_LOGOUT_TOKEN_EVENT_NAME, Map.of()))
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
