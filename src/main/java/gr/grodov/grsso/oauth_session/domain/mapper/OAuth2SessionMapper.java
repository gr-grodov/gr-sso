package gr.grodov.grsso.oauth_session.domain.mapper;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_session.service.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.domain.entity.OAuth2Session;
import gr.grodov.grsso.oauth_session.service.DeviceResolveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OAuth2SessionMapper implements Mapper<OAuth2Session, OAuth2SessionDto> {

    private final DeviceResolveService deviceResolveService;

    @Override
    public OAuth2SessionDto fromDB(OAuth2Session entity) {
        return OAuth2SessionDto.builder()
            .sid(entity.getSid())
            .authorizationId(entity.getAuthorizationId())
            .userId(entity.getUserId())
            .clientId(entity.getClientId())
            .clientName(entity.getClientName())
            .deviceIpAddress(entity.getDeviceIpAddress())
            .deviceLocationCountry(entity.getDeviceLocationCountry())
            .deviceLocationCity(entity.getDeviceLocationCity())
            .deviceUserAgent(entity.getDeviceUserAgent())
            .deviceType(entity.getDeviceType())
            .deviceInfo(deviceResolveService.deviceInfo(entity.getDeviceUserAgent()))
            .lastUsedAt(entity.getLastUsedAt())
            .diffNowAndLastEnter(Instant.now().getEpochSecond() - entity.getLastUsedAt().getEpochSecond())
        .build();
    }

    @Override
    public OAuth2Session toDB(OAuth2SessionDto dto) {
        return OAuth2Session.builder()
            .sid(dto.sid())
            .authorizationId(dto.authorizationId())
            .userId(dto.userId())
            .clientId(dto.clientId())
            .clientName(dto.clientName())
            .deviceIpAddress(dto.deviceIpAddress())
            .deviceLocationCountry(dto.deviceLocationCountry())
            .deviceLocationCity(dto.deviceLocationCity())
            .deviceUserAgent(dto.deviceUserAgent())
            .deviceType(dto.deviceType())
            .lastUsedAt(dto.lastUsedAt())
        .build();
    }
}
