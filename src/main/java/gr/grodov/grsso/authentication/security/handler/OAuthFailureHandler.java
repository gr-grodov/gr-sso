package gr.grodov.grsso.authentication.security.handler;

import gr.grodov.grsso.common.props.FrontendAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthFailureHandler implements AuthenticationFailureHandler {

    private final FrontendAppProperties properties;

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
            .fromUri(URI.create(properties.url()))
            .path(properties.endpoints().providerError())
            .queryParam("code", error)
            .build()
            .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
