package gr.grodov.grsso.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException exception
    ) throws IOException, ServletException {
        String error = switch (exception) {
            case OAuth2AuthenticationException e -> switch (e.getError().getErrorCode()) {
                case "access_denied" -> "access_denied";
                case "invalid_grant", "invalid_client" -> "invalid_grant";
                default -> "unknown";
            };
            default -> "unknown";
        };

        log.info("Invalid oauth2 ", exception);
        response.sendRedirect("/provider-error?error=" + error);
    }
}
