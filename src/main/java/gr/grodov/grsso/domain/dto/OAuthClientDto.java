package gr.grodov.grsso.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.grodov.grsso.domain.entities.oauth.*;
import lombok.Builder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Builder
public record OAuthClientDto(
    String id,
    String clientId,
    @JsonIgnore
    Instant clientIdIssuedAt,
    @JsonIgnore
    String clientSecret,
    @JsonIgnore
    Instant clientSecretExpiresAt,
    String clientName,
    Set<OAuthClientAuthenticationMethod> clientAuthenticationMethods,
    Set<OAuthAuthorizationGrantType> authorizationGrantTypes,
    Set<String> redirectUris,
    Set<String> postLogoutRedirectUris,
    Set<String> scopes,
    OAuthClientSettings clientSettings,
    OAuthTokenSettings tokenSettings,
    OAuthClientStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
