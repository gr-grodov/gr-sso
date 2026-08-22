package gr.grodov.grsso.authorization_sso.security.utils;

import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ScopedClaimsBuilder {

    public Map<String, Object> idTokenClaims(UserInfoDto user, Set<String> scopes) {
        Map<String, Object> claims = new HashMap<>();

        if (scopes.contains(OAuthScope.OPEN_ID.getScopeValue())) {
            claims.put(StandardClaimNames.SUB, user.id().toString());
        }

        if (scopes.contains(OAuthScope.EMAIL.getScopeValue())) {
            claims.put(StandardClaimNames.EMAIL, user.email());
        }

        return claims;
    }

    public Map<String, Object> accessTokenClaims(UserInfoDto user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(StandardClaimNames.SUB, user.id().toString());
        return claims;
    }
}
