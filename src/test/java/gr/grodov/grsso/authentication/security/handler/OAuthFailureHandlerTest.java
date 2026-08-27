package gr.grodov.grsso.authentication.security.handler;

import gr.grodov.grsso.common.props.FrontendAppProperties;
import gr.grodov.grsso.integration.utils.URIParseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthFailureHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FrontendAppProperties properties;
    @Mock
    private FrontendAppProperties.Endpoints endpoints;
    @InjectMocks
    private OAuthFailureHandler oAuthFailureHandler;

    @Test
    void onAuthenticationFailure_withAccessDenied_redirectWithCode() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        when(properties.endpoints()).thenReturn(endpoints);
        when(properties.endpoints().providerError()).thenReturn("/provider-error");
        ArgumentCaptor<String> redirectUri = ArgumentCaptor.forClass(String.class);
        var exception = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED));

        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(redirectUri.capture());

        var queryParams = URIParseUtils.getQueryParams(URI.create(redirectUri.getValue()));
        assertThat(queryParams.getFirst("code")).isEqualTo("access_denied");
    }

    @Test
    void onAuthenticationFailure_withInvalidGrant_redirectWithCode() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        when(properties.endpoints()).thenReturn(endpoints);
        when(properties.endpoints().providerError()).thenReturn("/provider-error");
        ArgumentCaptor<String> redirectUri = ArgumentCaptor.forClass(String.class);
        var exception = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_GRANT));

        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(redirectUri.capture());

        var queryParams = URIParseUtils.getQueryParams(URI.create(redirectUri.getValue()));
        assertThat(queryParams.getFirst("code")).isEqualTo("invalid_grant");
    }

    @Test
    void onAuthenticationFailure_withInvalidClient_redirectWithCode() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        when(properties.endpoints()).thenReturn(endpoints);
        when(properties.endpoints().providerError()).thenReturn("/provider-error");
        ArgumentCaptor<String> redirectUri = ArgumentCaptor.forClass(String.class);
        var exception = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT));

        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(redirectUri.capture());

        var queryParams = URIParseUtils.getQueryParams(URI.create(redirectUri.getValue()));
        assertThat(queryParams.getFirst("code")).isEqualTo("invalid_grant");
    }

    @Test
    void onAuthenticationFailure_withServerError_redirectWithCode() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        when(properties.endpoints()).thenReturn(endpoints);
        when(properties.endpoints().providerError()).thenReturn("/provider-error");
        ArgumentCaptor<String> redirectUri = ArgumentCaptor.forClass(String.class);
        var exception = new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR));

        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(redirectUri.capture());

        var queryParams = URIParseUtils.getQueryParams(URI.create(redirectUri.getValue()));
        assertThat(queryParams.getFirst("code")).isEqualTo("unknown");
    }

    @Test
    void onAuthenticationFailure_withNoOAuth2AuthenticationException_redirectWithCode() throws IOException {
        when(properties.url()).thenReturn("https://gr-sso-front.com");
        when(properties.endpoints()).thenReturn(endpoints);
        when(properties.endpoints().providerError()).thenReturn("/provider-error");
        ArgumentCaptor<String> redirectUri = ArgumentCaptor.forClass(String.class);
        var exception = new DisabledException("");

        oAuthFailureHandler.onAuthenticationFailure(request, response, exception);
        verify(response).sendRedirect(redirectUri.capture());

        var queryParams = URIParseUtils.getQueryParams(URI.create(redirectUri.getValue()));
        assertThat(queryParams.getFirst("code")).isEqualTo("unknown");
    }
}