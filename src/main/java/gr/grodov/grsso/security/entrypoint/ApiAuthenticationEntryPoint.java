package gr.grodov.grsso.security.entrypoint;

import gr.grodov.grsso.api.dto.response.ErrorResponse;
import gr.grodov.grsso.security.utils.ApiResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiResponseWriter writer;

    @Override
    public void commence(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException authException
    ) throws IOException {
        writer.write(response, HttpStatus.UNAUTHORIZED, ErrorResponse.of("unauthorized"));
    }
}
