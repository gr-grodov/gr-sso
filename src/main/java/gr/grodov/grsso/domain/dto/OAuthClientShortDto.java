package gr.grodov.grsso.domain.dto;

import gr.grodov.grsso.domain.entities.oauth.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OAuthClientShortDto(
    String id,
    String clientId,
    String clientName,
    Set<OAuthAuthorizationGrantType> authorizationGrantTypes,
    Set<String> redirectUris,
    Set<String> scopes,
    OAuthClientStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
