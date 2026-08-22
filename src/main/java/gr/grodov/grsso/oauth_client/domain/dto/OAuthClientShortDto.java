package gr.grodov.grsso.oauth_client.domain.dto;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientStatus;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
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
