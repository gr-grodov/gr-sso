package gr.grodov.grsso.oauth_session.service.dto;

import gr.grodov.grsso.oauth_session.domain.entity.DeviceType;

public record DeviceContext(
    String deviceId,
    String deviceIpAddress,
    String deviceUserAgent,
    DeviceType deviceType
) {
}
