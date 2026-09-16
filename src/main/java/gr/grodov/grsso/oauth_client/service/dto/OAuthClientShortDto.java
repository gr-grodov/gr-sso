package gr.grodov.grsso.oauth_client.service.dto;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientStatus;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@NamedInterface("service")
@Builder
public record OAuthClientShortDto(
    String id,
    String clientId,
    String clientName,
    Set<OAuthScope> scopes,
    OAuthClientStatus status,
    Instant createdAt,
    Instant updatedAt,
    UUID avatarId
) {

}
