package gr.grodov.grsso.authentication.security.handler;

import gr.grodov.grsso.common.props.FrontendAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        response.sendRedirect(properties.url());
    }
}
