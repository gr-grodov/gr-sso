package gr.grodov.grsso.oauth_client.domain.mapper;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_client.service.dto.OAuthClientShortDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClient;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientShortMapper implements Mapper<OAuthClient, OAuthClientShortDto> {

    @Override
    public OAuthClientShortDto fromDB(OAuthClient oAuthClient) {
        return OAuthClientShortDto.builder()
            .id(oAuthClient.getId())
            .clientId(oAuthClient.getClientId())
            .clientName(oAuthClient.getClientName())
            .scopes(oAuthClient.getScopes())
            .status(oAuthClient.getStatus())
            .createdAt(oAuthClient.getCreatedAt())
            .updatedAt(oAuthClient.getUpdatedAt())
        .build();
    }

    @Override
    public OAuthClient toDB(OAuthClientShortDto oAuthClient) {
        return OAuthClient.builder()
            .id(oAuthClient.id())
            .clientId(oAuthClient.clientId())
            .clientName(oAuthClient.clientName())
            .scopes(oAuthClient.scopes())
            .status(oAuthClient.status())
            .createdAt(oAuthClient.createdAt())
            .updatedAt(oAuthClient.updatedAt())
        .build();
    }
}
