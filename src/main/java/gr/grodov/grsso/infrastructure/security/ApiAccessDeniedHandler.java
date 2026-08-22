package gr.grodov.grsso.infrastructure.security;

import gr.grodov.grsso.common.api.ErrorResponse;
import gr.grodov.grsso.common.utils.ApiResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    private final ApiResponseWriter writer;

    @Override
    public void handle(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {
        writer.write(response, HttpStatus.FORBIDDEN, ErrorResponse.of("forbidden"));
    }
}
