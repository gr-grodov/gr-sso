package gr.grodov.grsso.authorization_sso.security.config;

import gr.grodov.grsso.authorization_sso.service.OAuth2FlowService;
import gr.grodov.grsso.common.props.FrontendAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuthAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private OAuth2FlowService oAuth2FlowService;
    @Mock
    private FrontendAppProperties frontendAppProperties;
    @InjectMocks
    private OAuthAuthenticationEntryPoint authenticationEntryPoint;

    @Test
    void commence_withException_redirectToFrontendLogin() throws IOException {
        when(httpRequest.getScheme()).thenReturn("https");
        when(httpRequest.getServerName()).thenReturn("grsso.com");
        when(httpRequest.getServerPort()).thenReturn(9090);
        when(httpRequest.getRequestURI()).thenReturn("/login/oauth2/code/grsso");
        when(frontendAppProperties.loginUrl()).thenReturn("https://gr-sso-front.com/login");

        authenticationEntryPoint.commence(httpRequest, httpResponse, new SessionAuthenticationException(""));

        verify(oAuth2FlowService).saveRedirectRequest(anyString());
        verify(httpResponse).sendRedirect("https://gr-sso-front.com/login");
    }
}