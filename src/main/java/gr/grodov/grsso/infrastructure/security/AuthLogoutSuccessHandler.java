package gr.grodov.grsso.infrastructure.security;

import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.common.utils.ApiResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ApiResponseWriter writer;

    @Override
    public void onLogoutSuccess(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @Nullable Authentication authentication
    ) throws IOException {
        writer.write(response, HttpStatus.OK, SuccessResponse.of(true));
    }
}
