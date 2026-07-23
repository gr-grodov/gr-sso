package gr.grodov.grsso.security.handler;

import gr.grodov.grsso.api.dto.response.ErrorResponse;
import gr.grodov.grsso.props.AppProperties;
import gr.grodov.grsso.security.utils.ApiResponseWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final AppProperties properties;

    @Override
    public void onAuthenticationFailure(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException exception
    ) throws IOException {
        String error = switch (exception) {
            case OAuth2AuthenticationException e -> switch (e.getError().getErrorCode()) {
                case "access_denied" -> "access_denied";
                case "invalid_grant", "invalid_client" -> "invalid_grant";
                default -> "unknown";
            };
            default -> "unknown";
        };

        String redirectUrl = UriComponentsBuilder
            .fromUri(URI.create(properties.frontUrl()))
            .path("/provider-error")
            .queryParam("code", error)
            .build()
            .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
