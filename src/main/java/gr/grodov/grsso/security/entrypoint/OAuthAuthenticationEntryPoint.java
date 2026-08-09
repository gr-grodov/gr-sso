package gr.grodov.grsso.security.entrypoint;

import gr.grodov.grsso.security.service.SessionService;
import gr.grodov.grsso.service.OAuthAuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final OAuthAuthorizationService authorizationService;
    private final SessionService sessionService;

    @Override
    public void commence(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException authException
    ) throws IOException {

        String requestURI = request.getRequestURI();
        if (request.getQueryString() != null) {
            requestURI += "?" + request.getQueryString();
        }


        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAa");
        String id = authorizationService.saveRequest(requestURI);
        sessionService.setAttribute(SessionService.Attributes.OAUTH_FLOW, id);
        response.sendRedirect("http://localhost:5173/login");
    }
}
