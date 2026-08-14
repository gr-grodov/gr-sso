package gr.grodov.grsso.security.handler;

import gr.grodov.grsso.props.FrontendAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final FrontendAppProperties properties;

    @Override
    public void onAuthenticationSuccess(
        @NonNull HttpServletRequest request,
        HttpServletResponse response,
        @NonNull Authentication authentication
    ) throws IOException {
        response.sendRedirect(properties.loginUrl());
    }
}
