package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.domain.entities.oauth.OAuthClient;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientShortMapper implements Mapper<OAuthClient, OAuthClientShortDto>{

    @Override
    public OAuthClientShortDto fromDB(OAuthClient oAuthClient) {
        return OAuthClientShortDto.builder()
            .id(oAuthClient.getId())
            .clientId(oAuthClient.getClientId())
            .clientName(oAuthClient.getClientName())
            .authorizationGrantTypes(oAuthClient.getAuthorizationGrantTypes())
            .redirectUris(oAuthClient.getRedirectUris())
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
            .authorizationGrantTypes(oAuthClient.authorizationGrantTypes())
            .redirectUris(oAuthClient.redirectUris())
            .scopes(oAuthClient.scopes())
            .status(oAuthClient.status())
            .createdAt(oAuthClient.createdAt())
            .updatedAt(oAuthClient.updatedAt())
        .build();
    }
}
