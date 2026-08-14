package gr.grodov.grsso.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.grodov.grsso.domain.entities.oauth_client.*;
import lombok.Builder;

import java.time.Instant;
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
    Set<OAuthScope> scopes,
    OAuthClientSettings clientSettings,
    OAuthTokenSettings tokenSettings,
    OAuthClientStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
