package gr.grodov.grsso.security.handler;

import gr.grodov.grsso.api.dto.response.ErrorResponse;
import gr.grodov.grsso.security.utils.ApiResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final ApiResponseWriter writer;

    @Override
    public void onAuthenticationFailure(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException exception
    ) throws IOException {
        String error = switch (exception) {
            case BadCredentialsException _ -> "bad_credentials";
            case DisabledException _ -> "disabled";
            case LockedException _ -> "locked";
            default -> "unknown";
        };

        writer.write(response, HttpStatus.FORBIDDEN, ErrorResponse.of(error));
    }
}
