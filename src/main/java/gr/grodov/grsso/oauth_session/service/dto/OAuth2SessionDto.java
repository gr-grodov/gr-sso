package gr.grodov.grsso.oauth_session.service.dto;

import gr.grodov.grsso.oauth_session.domain.entity.DeviceType;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.UUID;

@NamedInterface("service")
@Builder
public record OAuth2SessionDto(
    UUID sid,
    String authorizationId,
    UUID userId,
    String clientId,
    String clientName,
    Boolean isCurrentDevice,
    String deviceIpAddress,
    String deviceLocationCountry,
    String deviceLocationCity,
    String deviceUserAgent,
    DeviceType deviceType,
    DeviceInfo deviceInfo,
    Instant lastUsedAt,
    Long diffNowAndLastEnter
) {
    public OAuth2SessionDto withCurrentDeviceFlag(boolean isCurrDevice) {
        return OAuth2SessionDto.builder()
            .sid(sid)
            .authorizationId(authorizationId)
            .userId(userId)
            .clientId(clientId)
            .clientName(clientName)
            .isCurrentDevice(isCurrDevice)
            .deviceIpAddress(deviceIpAddress)
            .deviceLocationCountry(deviceLocationCountry)
            .deviceLocationCity(deviceLocationCity)
            .deviceUserAgent(deviceUserAgent)
            .deviceType(deviceType)
            .deviceInfo(deviceInfo)
            .lastUsedAt(lastUsedAt)
            .diffNowAndLastEnter(diffNowAndLastEnter)
        .build();
    }
}
