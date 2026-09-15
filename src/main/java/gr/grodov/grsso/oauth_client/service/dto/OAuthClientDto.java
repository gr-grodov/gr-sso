package gr.grodov.grsso.oauth_client.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@NamedInterface("service")
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
    Instant updatedAt,
    UUID avatarId
) {
}
