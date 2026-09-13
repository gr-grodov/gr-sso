package gr.grodov.grsso.authorization_sso.security.utils;

import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScopedClaimsBuilder {

    public static Map<String, Object> idTokenClaims(UserInfoDto user, Set<String> scopes) {
        Map<String, Object> claims = new HashMap<>();

        if (scopes.contains(OAuthScope.OPEN_ID.getScopeValue())) {
            claims.put(StandardClaimNames.SUB, user.id().toString());
        }

        if (scopes.contains(OAuthScope.EMAIL.getScopeValue())) {
            claims.put(StandardClaimNames.EMAIL, user.email());
        }

        return claims;
    }

    public static Map<String, Object> accessTokenClaims(UserInfoDto user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(StandardClaimNames.SUB, user.id().toString());
        return claims;
    }
}
