package gr.grodov.grsso.authorization_sso.security.config;

import gr.grodov.grsso.oauth_session.service.DeviceResolveService;
import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthorizationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final OAuth2AuthorizationService authorizationService;
    private final OAuth2SessionService sessionService;
    private final DeviceResolveService deviceResolveService;

    @Override
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Authentication authentication
    ) throws IOException {
        if (authentication instanceof OAuth2AuthorizationCodeRequestAuthenticationToken authenticationToken) {
            sessionService.createOrUpdateSession(extractAuthorizationId(authenticationToken), deviceResolveService.deviceContext(request));
            sendAuthorizationResponse(request, response, authenticationToken);
        }
    }

    private void sendAuthorizationResponse(
        HttpServletRequest request,
        HttpServletResponse response,
        OAuth2AuthorizationCodeRequestAuthenticationToken authentication
    ) throws IOException {
        String redirectUriForResponse = authentication.getRedirectUri();
        if (redirectUriForResponse == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REDIRECT_URI);
        }
        OAuth2AuthorizationCode authorizationCode = getAuthorizationCode(authentication);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(redirectUriForResponse)
            .queryParam(OAuth2ParameterNames.CODE, authorizationCode.getTokenValue());
        if (StringUtils.hasText(authentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(authentication.getState(), StandardCharsets.UTF_8));
        }

        String redirectUri = uriBuilder.build(true).toUriString();
        this.redirectStrategy.sendRedirect(request, response, redirectUri);
    }

    private @NotNull OAuth2Authorization extractAuthorizationId(OAuth2AuthorizationCodeRequestAuthenticationToken authentication) {
        OAuth2AuthorizationCode authorizationCode = getAuthorizationCode(authentication);

        OAuth2Authorization authorization = authorizationService.findByToken(authorizationCode.getTokenValue(), new OAuth2TokenType(OAuth2ParameterNames.CODE));
        if (authorization == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }
        return authorization;
    }

    private OAuth2AuthorizationCode getAuthorizationCode(OAuth2AuthorizationCodeRequestAuthenticationToken authentication) {
        OAuth2AuthorizationCode authorizationCode = authentication.getAuthorizationCode();
        if (authorizationCode == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
        }
        return authorizationCode;
    }
}
