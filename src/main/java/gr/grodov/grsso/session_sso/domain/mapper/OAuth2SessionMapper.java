package gr.grodov.grsso.session_sso.domain.mapper;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.session_sso.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.session_sso.domain.entity.OAuth2Session;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SessionMapper implements Mapper<OAuth2Session, OAuth2SessionDto> {

    @Override
    public OAuth2SessionDto fromDB(OAuth2Session entity) {
        return OAuth2SessionDto.builder()
            .sid(entity.getSid())
            .authorizationId(entity.getAuthorizationId())
            .userId(entity.getUserId())
            .clientId(entity.getClientId())
            .clientName(entity.getClientName())
            .deviceId(entity.getDeviceId())
            .deviceIpAddress(entity.getDeviceIpAddress())
            .deviceUserAgent(entity.getDeviceUserAgent())
            .lastUsedAt(entity.getLastUsedAt())
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
            .deviceId(dto.deviceId())
            .deviceIpAddress(dto.deviceIpAddress())
            .deviceUserAgent(dto.deviceUserAgent())
        .build();
    }
}
