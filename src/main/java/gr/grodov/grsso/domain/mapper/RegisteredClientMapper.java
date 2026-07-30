package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.entities.oauth.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth.OAuthClient;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientAuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.ConfigurationSettingNames;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RegisteredClientMapper implements Mapper<OAuthClient, RegisteredClient> {

    @Override
    public RegisteredClient fromDB(OAuthClient client) {
        return RegisteredClient.withId(client.getId())
            .clientId(client.getClientId())
            .clientIdIssuedAt(client.getClientIdIssuedAt())
            .clientSecret(client.getClientSecret())
            .clientSecretExpiresAt(client.getClientSecretExpiresAt())
            .clientName(client.getClientName())
            .clientAuthenticationMethods(methods -> methods.addAll(
                fromClientAuthenticationMethods(client.getClientAuthenticationMethods())
            ))
            .authorizationGrantTypes(types -> types.addAll(
                fromAuthorizationGrantTypes(client.getAuthorizationGrantTypes())
            ))
            .redirectUris(uris -> uris.addAll(client.getRedirectUris()))
            .postLogoutRedirectUris(uris -> uris.addAll(client.getPostLogoutRedirectUris()))
            .scopes(scopes -> scopes.addAll(client.getScopes()))
            .clientSettings(fromClientSettings(client.getClientSettings()))
            .tokenSettings(fromTokenSettings(client.getClientSettings()))
        .build();
    }

    @Override
    public OAuthClient toDB(RegisteredClient client) {
        return OAuthClient.builder()
                .id(client.getId())
            .clientId(client.getClientId())
            .clientIdIssuedAt(client.getClientIdIssuedAt())
            .clientSecret(client.getClientSecret())
            .clientSecretExpiresAt(client.getClientSecretExpiresAt())
            .clientName(client.getClientName())
            .clientAuthenticationMethods(toClientAuthenticationMethods(client.getClientAuthenticationMethods()))
            .authorizationGrantTypes(toAuthorizationGrantTypes(client.getAuthorizationGrantTypes()))
            .redirectUris(client.getRedirectUris())
            .postLogoutRedirectUris(client.getPostLogoutRedirectUris())
            .scopes(client.getScopes())
            .clientSettings(toClientSettings(client.getClientSettings()))
            .tokenSettings(toTokenSettings(client.getTokenSettings()))
        .build();
    }

    private ClientSettings fromClientSettings(Map<String, Object> settings) {
        return ClientSettings.withSettings(settings).build();
    }

    private Map<String, Object> toClientSettings(ClientSettings settings) {
        return settings.getSettings();
    }

    private TokenSettings fromTokenSettings(Map<String, Object> settings) {
        TokenSettings.Builder tokenSettingBuilder = TokenSettings.withSettings(settings);
        if (!settings.containsKey(ConfigurationSettingNames.Token.ACCESS_TOKEN_FORMAT)) {
            tokenSettingBuilder.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED);
        }
        return tokenSettingBuilder.build();
    }

    private Map<String, Object> toTokenSettings(TokenSettings settings) {
        return settings.getSettings();
    }

    private Set<ClientAuthenticationMethod> fromClientAuthenticationMethods(Set<OAuthClientAuthenticationMethod> methods) {
        return methods.stream()
            .map(method -> ClientAuthenticationMethod.valueOf(method.getValue()))
            .collect(Collectors.toSet());
    }

    private Set<OAuthClientAuthenticationMethod> toClientAuthenticationMethods(Set<ClientAuthenticationMethod> methods) {
        return methods.stream()
            .map(method -> OAuthClientAuthenticationMethod.getByValue(method.getValue()))
            .collect(Collectors.toSet());
    }

    private Set<AuthorizationGrantType> fromAuthorizationGrantTypes(Set<OAuthAuthorizationGrantType> types) {
        return types.stream()
            .map(type -> valueOfAuthorizationGrantType(type.getValue()))
            .collect(Collectors.toSet());
    }

    private Set<OAuthAuthorizationGrantType> toAuthorizationGrantTypes(Set<AuthorizationGrantType> types) {
        return types.stream()
            .map(type -> OAuthAuthorizationGrantType.getByValue(type.getValue()))
            .collect(Collectors.toSet());
    }

    private AuthorizationGrantType valueOfAuthorizationGrantType(String valueGrantType) {
        AuthorizationGrantType[] types = new AuthorizationGrantType[]{
            AuthorizationGrantType.AUTHORIZATION_CODE,
            AuthorizationGrantType.REFRESH_TOKEN,
            AuthorizationGrantType.CLIENT_CREDENTIALS,
            AuthorizationGrantType.JWT_BEARER,
            AuthorizationGrantType.DEVICE_CODE,
            AuthorizationGrantType.TOKEN_EXCHANGE
        };

        for (AuthorizationGrantType grantType : types) {
            if (grantType.getValue().equals(valueGrantType)) {
                return grantType;
            }
        }
        return new AuthorizationGrantType(valueGrantType);
    }
}
