package gr.grodov.grsso.security.service.oauth2;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.security.principal.UserPrincipal;
import gr.grodov.grsso.service.UserInfoService;
import gr.grodov.grsso.service.exceptions.OAuthPrincipalNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthClaimsService {

    private final ScopedClaimsBuilder claimsBuilder;
    private final UserInfoService userInfoService;

    public Map<String, Object> tokenClaims(OAuth2TokenContext context) {
        Authentication authentication = getUserFromContext(context);
        UserInfoDto userInfo = userInfoService.findById(authentication.getName());

        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            return claimsBuilder.accessTokenClaims(userInfo);
        }

        return claimsBuilder.idTokenClaims(userInfo, context.getAuthorizedScopes());
    }

    public Map<String, Object> userInfoClaims(OidcUserInfoAuthenticationContext context) {
        OAuth2Authorization authorization = context.getAuthorization();
        Authentication authentication = getUserFromContext(authorization);
        UserInfoDto userInfo = userInfoService.findById(authentication.getName());

        return claimsBuilder.idTokenClaims(userInfo, authorization.getAuthorizedScopes());
    }

    private Authentication getUserFromContext(OAuth2TokenContext context) {
        if (context.getPrincipal() instanceof Authentication authentication) {
            return authentication;
        }

        if (context.getPrincipal() != null && context.getPrincipal().getPrincipal() instanceof Authentication authentication) {
            return authentication;
        }

        throw new OAuthPrincipalNotFoundException();
    }

    private Authentication getUserFromContext(OAuth2Authorization authorization) {
        if (authorization.getAttribute(Principal.class.getName()) instanceof Authentication authentication) {
            return authentication;
        }

        throw new OAuthPrincipalNotFoundException();
    }
}
