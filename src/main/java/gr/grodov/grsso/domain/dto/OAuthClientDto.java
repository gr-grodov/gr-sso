package gr.grodov.grsso.domain.dto;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record OAuthClientDto(
    String id,
    String clientId,
    Instant clientIdIssuedAt,
    String clientSecret,
    Instant clientSecretExpiresAt,
    String clientName,
    Set<String> clientAuthenticationMethods,
    Set<String> authorizationGrantTypes,
    Set<String> redirectUris,
    Set<String> scopes
) {
}
