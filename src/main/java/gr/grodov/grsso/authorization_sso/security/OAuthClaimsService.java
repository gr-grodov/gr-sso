package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.authorization_sso.security.utils.ScopedClaimsBuilder;
import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.service.UserInfoService;
import gr.grodov.grsso.authorization_sso.exception.OAuthPrincipalNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuthClaimsService {

    private final UserInfoService userInfoService;
    private final OAuth2SessionService sessionService;

    public Map<String, Object> tokenClaims(OAuth2TokenContext context) {
        Authentication authentication = getUserFromContext(context);
        UserInfoDto userInfo = userInfoService.findById(authentication.getName());

        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            return ScopedClaimsBuilder.accessTokenClaims(userInfo);
        }

        Map<String, Object> tokenClaims = ScopedClaimsBuilder.idTokenClaims(userInfo, context.getAuthorizedScopes());
        if (context.getTokenType().getValue().equals("id_token")) {
            tokenClaims.put(LogoutTokenClaimNames.SID, sessionService.getSID(Objects.requireNonNull(context.getAuthorization())));
        }

        return tokenClaims;
    }

    public Map<String, Object> userInfoClaims(OidcUserInfoAuthenticationContext context) {
        OAuth2Authorization authorization = context.getAuthorization();
        Authentication authentication = getUserFromContext(authorization);
        UserInfoDto userInfo = userInfoService.findById(authentication.getName());

        return ScopedClaimsBuilder.idTokenClaims(userInfo, authorization.getAuthorizedScopes());
    }

    private Authentication getUserFromContext(OAuth2TokenContext context) {
        if (context.getPrincipal() instanceof Authentication authentication) {
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
