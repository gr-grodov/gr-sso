package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.entities.OAuthClient;
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
            .scopes(oAuthClient.getScopes())
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
            .scopes(oAuthClient.scopes())
        .build();
    }
}
