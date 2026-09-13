package gr.grodov.grsso.oauth_client.domain.mapper;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_client.service.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClient;
import org.springframework.stereotype.Component;

@Component
public class OAuthClientMapper implements Mapper<OAuthClient, OAuthClientDto> {

    @Override
    public OAuthClientDto fromDB(OAuthClient oAuthClient) {
        return OAuthClientDto.builder()
            .id(oAuthClient.getId())
            .clientId(oAuthClient.getClientId())
            .clientIdIssuedAt(oAuthClient.getClientIdIssuedAt())
            .clientSecret(oAuthClient.getClientSecret())
            .clientSecretExpiresAt(oAuthClient.getClientSecretExpiresAt())
            .clientName(oAuthClient.getClientName())
            .clientAuthenticationMethods(oAuthClient.getClientAuthenticationMethods())
            .authorizationGrantTypes(oAuthClient.getAuthorizationGrantTypes())
            .redirectUris(oAuthClient.getRedirectUris())
            .postLogoutRedirectUris(oAuthClient.getPostLogoutRedirectUris())
            .scopes(oAuthClient.getScopes())
            .clientSettings(oAuthClient.getClientSettings())
            .tokenSettings(oAuthClient.getTokenSettings())
            .status(oAuthClient.getStatus())
            .createdAt(oAuthClient.getCreatedAt())
            .updatedAt(oAuthClient.getUpdatedAt())
        .build();
    }

    @Override
    public OAuthClient toDB(OAuthClientDto oAuthClient) {
        return OAuthClient.builder()
            .id(oAuthClient.id())
            .clientId(oAuthClient.clientId())
            .clientIdIssuedAt(oAuthClient.clientIdIssuedAt())
            .clientSecret(oAuthClient.clientSecret())
            .clientSecretExpiresAt(oAuthClient.clientSecretExpiresAt())
            .clientName(oAuthClient.clientName())
            .clientAuthenticationMethods(oAuthClient.clientAuthenticationMethods())
            .authorizationGrantTypes(oAuthClient.authorizationGrantTypes())
            .redirectUris(oAuthClient.redirectUris())
            .postLogoutRedirectUris(oAuthClient.postLogoutRedirectUris())
            .scopes(oAuthClient.scopes())
            .clientSecret(oAuthClient.clientSecret())
            .tokenSettings(oAuthClient.tokenSettings())
            .status(oAuthClient.status())
            .createdAt(oAuthClient.createdAt())
            .updatedAt(oAuthClient.updatedAt())
        .build();
    }
}
