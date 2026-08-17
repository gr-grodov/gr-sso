package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.props.OAuthAppProperties;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OAuthPropertiesService {
    private final OAuthAppProperties oAuthAppProperties;

    public Set<OAuthClientAuthenticationMethod> authenticationMethods() {
        return Arrays.stream(OAuthClientAuthenticationMethod.values())
            .filter(method ->
                oAuthAppProperties.authenticationMethods().contains(method.name())
            ).collect(Collectors.toSet());
    }

    public Set<OAuthAuthorizationGrantType> authorizationGrantTypes() {
        return Arrays.stream(OAuthAuthorizationGrantType.values())
            .filter(type ->
                oAuthAppProperties.authenticationGrantTypes().contains(type.name())
            ).collect(Collectors.toSet());
    }

    public boolean validAuthenticationMethods(@NotNull Set<OAuthClientAuthenticationMethod> methods) {
        for (OAuthClientAuthenticationMethod method: methods) {
            if (!authenticationMethods().contains(method)) {
                return false;
            }
        }
        return true;
    }

    public boolean validAuthorizationGrantTypes(@NotNull Set<OAuthAuthorizationGrantType> types) {
        for (OAuthAuthorizationGrantType type: types) {
            if (!authorizationGrantTypes().contains(type)) {
                return false;
            }
        }
        return true;
    }

    public Set<OAuthClientAuthenticationMethod> filterAuthenticationMethods(@NotNull Set<OAuthClientAuthenticationMethod> methods) {
        return authenticationMethods().stream().filter(methods::contains).collect(Collectors.toSet());
    }

    public Set<OAuthAuthorizationGrantType> filterAuthorizationGrantTypes(@NotNull Set<OAuthAuthorizationGrantType> types) {
        return authorizationGrantTypes().stream().filter(types::contains).collect(Collectors.toSet());
    }
}
