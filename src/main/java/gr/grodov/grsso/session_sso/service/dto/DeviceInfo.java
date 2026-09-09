package gr.grodov.grsso.session_sso.service.dto;

import nl.basjes.parse.useragent.classify.DeviceClass;

public record DeviceInfo(
    DeviceClass device,
    String operationSystem,
    String agent,
    String agentNameVersion
) {
}
