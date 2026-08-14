package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.entities.oauth_client.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
            .scopes(scopes -> scopes.addAll(client.getScopes().stream()
                .map(OAuthScope::getScopeValue)
                .collect(Collectors.toSet()))
            )
            .clientSettings(fromClientSettings(client.getClientSettings()))
            .tokenSettings(fromTokenSettings(client.getTokenSettings()))
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
            .scopes(client.getScopes().stream()
                .map(OAuthScope::scopeValueOf)
                .collect(Collectors.toSet())
            )
            .clientSettings(toClientSettings(client.getClientSettings()))
            .tokenSettings(toTokenSettings(client.getTokenSettings()))
        .build();
    }

    private ClientSettings fromClientSettings(OAuthClientSettings settings) {
        ClientSettings.Builder builder = ClientSettings.builder()
            .requireAuthorizationConsent(settings.isRequireAuthorizationConsent())
            .requireProofKey(settings.isRequireProofKey());

        if (settings.getJwkSetUrl() != null) {
            builder.jwkSetUrl(settings.getJwkSetUrl());
        }

        String signingAlgorithm = settings.getTokenEndpointAuthenticationSigningAlgorithm();
        if (signingAlgorithm != null) {
            builder.tokenEndpointAuthenticationSigningAlgorithm(SignatureAlgorithm.from(signingAlgorithm));
        }

        return builder.build();
    }

    private OAuthClientSettings toClientSettings(ClientSettings settings) {
        JwsAlgorithm algorithm = settings.getTokenEndpointAuthenticationSigningAlgorithm();

        return OAuthClientSettings.builder()
            .requireAuthorizationConsent(settings.isRequireAuthorizationConsent())
            .requireProofKey(settings.isRequireProofKey())
            .jwkSetUrl(settings.getJwkSetUrl())
            .tokenEndpointAuthenticationSigningAlgorithm(algorithm != null ? algorithm.getName() : "null")
        .build();
    }

    private TokenSettings fromTokenSettings(OAuthTokenSettings settings) {
        return TokenSettings.builder()
            .authorizationCodeTimeToLive(Duration.ofSeconds(settings.getAuthorizationCodeTimeToLive()))
            .accessTokenTimeToLive(Duration.ofSeconds(settings.getAccessTokenTimeToLive()))
            .refreshTokenTimeToLive(Duration.ofSeconds(settings.getRefreshTokenTimeToLive()))
            .reuseRefreshTokens(settings.isReuseRefreshTokens())
        .build();
    }

    private OAuthTokenSettings toTokenSettings(TokenSettings settings) {
        return OAuthTokenSettings.builder()
            .authorizationCodeTimeToLive(settings.getAuthorizationCodeTimeToLive().getSeconds())
            .accessTokenTimeToLive(settings.getAccessTokenTimeToLive().getSeconds())
            .refreshTokenTimeToLive(settings.getRefreshTokenTimeToLive().getSeconds())
            .reuseRefreshTokens(settings.isReuseRefreshTokens())
        .build();
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
