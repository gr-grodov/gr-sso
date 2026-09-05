package gr.grodov.grsso.common.utils;

public record DeviceContext(
    String deviceId,
    String deviceIpAddress,
    String deviceUserAgent
) {
}
