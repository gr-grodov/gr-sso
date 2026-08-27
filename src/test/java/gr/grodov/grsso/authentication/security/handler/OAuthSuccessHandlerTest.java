package gr.grodov.grsso.authentication.security.handler;

import gr.grodov.grsso.common.props.FrontendAppProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthSuccessHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;

    @Mock
    private FrontendAppProperties properties;
    @InjectMocks
    private OAuthSuccessHandler oAuthSuccessHandler;

    @Test
    void onAuthenticationSuccess_redirectToFrontend() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        ArgumentCaptor<String> redirectUrl = ArgumentCaptor.forClass(String.class);

        oAuthSuccessHandler.onAuthenticationSuccess(request, response, authentication);
        verify(response).sendRedirect(redirectUrl.capture());

        assertThat(redirectUrl.getValue()).isEqualTo("https://gr-sso-front.com");
    }
}