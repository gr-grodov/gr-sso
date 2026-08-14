package gr.grodov.grsso.domain.entities.oauth_client;

import gr.grodov.grsso.service.exceptions.OAuthInvalidScopeException;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.Arrays;

public enum OAuthScope {
    OPEN_ID(OidcScopes.OPENID),
    EMAIL(OidcScopes.EMAIL),
    PROFILE(OidcScopes.PROFILE);

    private final String scopeValue;

    OAuthScope(String value) {
        this.scopeValue = value;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    public static OAuthScope scopeValueOf(String scopeValue) {
        return Arrays.stream(OAuthScope.values())
            .filter(scope -> scope.scopeValue.equals(scopeValue))
            .findAny()
            .orElseThrow(OAuthInvalidScopeException::new);
    }
}
