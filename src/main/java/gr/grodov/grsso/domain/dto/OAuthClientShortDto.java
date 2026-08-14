package gr.grodov.grsso.domain.dto;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientStatus;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthScope;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder
public record OAuthClientShortDto(
    String id,
    String clientId,
    String clientName,
    Set<OAuthAuthorizationGrantType> authorizationGrantTypes,
    Set<String> redirectUris,
    Set<OAuthScope> scopes,
    OAuthClientStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
