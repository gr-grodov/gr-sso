package gr.grodov.grsso.session_sso.service.dto;

import gr.grodov.grsso.session_sso.domain.entity.DeviceType;
import org.springframework.modulith.NamedInterface;

public record DeviceContext(
    String deviceId,
    String deviceIpAddress,
    String deviceUserAgent,
    DeviceType deviceType
) {
}
