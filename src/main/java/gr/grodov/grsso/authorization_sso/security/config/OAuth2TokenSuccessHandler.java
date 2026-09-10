package gr.grodov.grsso.authorization_sso.security.config;

import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AccessTokenResponseAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2TokenSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler delegate = new OAuth2AccessTokenResponseAuthenticationSuccessHandler();
    private final OAuth2SessionService sessionService;
    private final OAuth2AuthorizationService authorizationService;

    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Authentication authentication
    ) {
        if (authentication instanceof OAuth2AccessTokenAuthenticationToken token) {
            OAuth2Authorization authorization = extractAuthorizationId(token);
            sessionService.updateSession(authorization);
        }

        delegate.onAuthenticationSuccess(request, response, authentication);
    }

    private @NotNull OAuth2Authorization extractAuthorizationId(OAuth2AccessTokenAuthenticationToken token) {
        String code = token.getAccessToken().getTokenValue();
        OAuth2Authorization authorization = authorizationService.findByToken(code, new OAuth2TokenType(OAuth2ParameterNames.ACCESS_TOKEN));
        if (authorization == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        return authorization;
    }
}
