package gr.grodov.grsso.session_sso.domain.dto;

import gr.grodov.grsso.session_sso.domain.entity.DeviceType;
import gr.grodov.grsso.session_sso.service.dto.DeviceInfo;
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
    String deviceLocationCountry,
    String deviceLocationCity,
    String deviceUserAgent,
    DeviceType deviceType,
    DeviceInfo deviceInfo,
    Instant lastUsedAt
) {
}
