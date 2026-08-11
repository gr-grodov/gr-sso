package gr.grodov.grsso.security.entrypoint;

import gr.grodov.grsso.service.OAuth2FlowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final OAuth2FlowService authorizationService;

    @Override
    public void commence(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException authException
    ) throws IOException {
        authorizationService.saveRedirectRequest(getAuthorizeRequest(request));
        // TODO вынести в config
        response.sendRedirect("http://localhost:5173/login");
    }

    private String getAuthorizeRequest(HttpServletRequest request) {
        String query = request.getQueryString();
        return String.format("%s%s",
            request.getRequestURL(),
            query != null ? ("?" + query) : ""
        );
    }
}
