package gr.grodov.grsso.domain.dto;

import gr.grodov.grsso.domain.entities.oauth.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientStatus;
import lombok.Builder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Builder
public record OAuthClientDto(
    String id,
    String clientId,
    Instant clientIdIssuedAt,
    String clientSecret,
    Instant clientSecretExpiresAt,
    String clientName,
    Set<OAuthClientAuthenticationMethod> clientAuthenticationMethods,
    Set<OAuthAuthorizationGrantType> authorizationGrantTypes,
    Set<String> redirectUris,
    Set<String> postLogoutRedirectUris,
    Set<String> scopes,
    Map<String, Object> clientSettings,
    Map<String, Object> tokenSettings,
    OAuthClientStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
