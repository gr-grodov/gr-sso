package gr.grodov.grsso.oauth_session.service.dto;

import nl.basjes.parse.useragent.classify.DeviceClass;

public record DeviceInfo(
    DeviceClass device,
    String operationSystem,
    String agent,
    String agentNameVersion
) {
}
