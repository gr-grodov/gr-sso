package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.entities.OAuthClient;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RegisteredClientMapper implements Mapper<RegisteredClient, OAuthClientDto> {
    @Override
    public OAuthClientDto fromDB(RegisteredClient client) {
        return OAuthClientDto.builder()
            .id(client.getId())
            .clientId(client.getClientId())
            .clientIdIssuedAt(client.getClientIdIssuedAt())
            .clientSecret(client.getClientSecret())
            .clientSecretExpiresAt(client.getClientSecretExpiresAt())
            .clientName(client.getClientName())
            .clientAuthenticationMethods(client.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet())
            )
            .authorizationGrantTypes(client.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet())
            )
            .redirectUris(client.getRedirectUris())
            .scopes(client.getScopes())
        .build();
    }

    @Override
    public RegisteredClient toDB(OAuthClientDto client) {
        return RegisteredClient
            .withId(client.id())
            .clientId(client.clientId())
            .clientIdIssuedAt(client.clientIdIssuedAt())
            .clientSecret(client.clientSecret())
            .clientSecretExpiresAt(client.clientSecretExpiresAt())
            .clientName(client.clientName())
            .clientAuthenticationMethods(methods ->
                methods.addAll(client.clientAuthenticationMethods().stream()
                    .map(ClientAuthenticationMethod::valueOf)
                    .toList()
                )
            )
            .authorizationGrantTypes(types ->
                types.addAll(client.authorizationGrantTypes().stream()
                    .map(this::valueOfAuthorizationGrantType)
                    .toList()
                )
            )
            .redirectUris(uris -> uris.addAll(client.redirectUris()))
            .scopes(scopes -> scopes.addAll(client.scopes()))
        .build();
    }

    private AuthorizationGrantType valueOfAuthorizationGrantType(String type) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(type)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(type)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(type)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.JWT_BEARER.getValue().equals(type)) {
            return AuthorizationGrantType.JWT_BEARER;
        } else if (AuthorizationGrantType.DEVICE_CODE.getValue().equals(type)) {
            return AuthorizationGrantType.DEVICE_CODE;
        } else if (AuthorizationGrantType.TOKEN_EXCHANGE.getValue().equals(type)) {
            return AuthorizationGrantType.TOKEN_EXCHANGE;
        } else {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

    }
}
