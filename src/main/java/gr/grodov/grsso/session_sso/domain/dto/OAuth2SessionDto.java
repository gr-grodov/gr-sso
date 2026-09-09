package gr.grodov.grsso.session_sso.domain.dto;

import gr.grodov.grsso.session_sso.domain.entity.DeviceType;
import gr.grodov.grsso.session_sso.service.dto.DeviceInfo;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.time.Instant;
import java.util.UUID;

@NamedInterface("service")
@Builder
public record OAuth2SessionDto(
    UUID sid,
    String authorizationId,
    Long userId,
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
