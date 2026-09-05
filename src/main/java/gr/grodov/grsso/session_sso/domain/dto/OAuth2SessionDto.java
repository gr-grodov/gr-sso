package gr.grodov.grsso.session_sso.domain.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OAuth2SessionDto(
    UUID sid,
    String authorizationId,
    Long userId,
    String clientId,
    String clientName,
    String deviceId,
    String deviceIpAddress,
    String deviceUserAgent,
    Instant lastUsedAt
) {
}
