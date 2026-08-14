package gr.grodov.grsso.security.entrypoint;

import gr.grodov.grsso.props.FrontendAppProperties;
import gr.grodov.grsso.service.OAuth2FlowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class OAuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final OAuth2FlowService authorizationService;
    private final FrontendAppProperties frontendAppProperties;

    private final FrontendAppProperties properties;

    @Override
    public void commence(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull AuthenticationException authException
    ) throws IOException {
        authorizationService.saveRedirectRequest(UrlUtils.buildFullRequestUrl(request));
        response.sendRedirect(properties.loginUrl());
    }
}
