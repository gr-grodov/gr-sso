package gr.grodov.grsso.oauth_client.domain.entity;

import gr.grodov.grsso.oauth_client.exception.OAuthInvalidScopeException;
import lombok.Getter;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.Arrays;

@NamedInterface("domain")
@Getter
public enum OAuthScope {
    OPEN_ID(OidcScopes.OPENID),
    EMAIL(OidcScopes.EMAIL),
    PROFILE(OidcScopes.PROFILE);

    private final String scopeValue;

    OAuthScope(String value) {
        this.scopeValue = value;
    }

    public static OAuthScope scopeValueOf(String scopeValue) throws OAuthInvalidScopeException {
        return Arrays.stream(OAuthScope.values())
            .filter(scope -> scope.scopeValue.equals(scopeValue))
            .findAny()
            .orElseThrow(OAuthInvalidScopeException::new);
    }
}
