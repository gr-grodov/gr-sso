package gr.grodov.grsso.session_sso.service.dto;

public record DeviceInfo(
    String device,
    String operationSystem,
    String agent,
    String agentNameVersion
) {
}
