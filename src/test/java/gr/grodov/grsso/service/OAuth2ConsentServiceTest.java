package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.service.exceptions.OAuth2ConsentException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2ConsentServiceTest {

    @Autowired
    @Mock
    private RestTemplate captureRedirectRestTemplate;
    @Mock
    private AuthorizationServerSettings authorizationServerSettings;
    @InjectMocks
    private OAuth2ConsentService service;
    @Mock
    private HttpServletRequest httpRequest;


    @Test
    void requestAuthorizationRedirect_withNoCookie_throwsOAuth2ConsentException() {
        var request = new OAuth2ConsentRequest();
        when(httpRequest.getHeader(HttpHeaders.COOKIE)).thenReturn(null);

        assertThatThrownBy(() -> service.requestAuthorizationRedirect(request, httpRequest))
            .isInstanceOf(OAuth2ConsentException.class)
            .satisfies(ex -> {
                var exception = (OAuth2ConsentException) ex;
                assertThat(exception.getCode()).isEqualTo("missing_cookie");
            });

        verifyNoInteractions(captureRedirectRestTemplate, authorizationServerSettings);
    }

    @Test
    void requestAuthorizationRedirect_withStatusErrorCode_throwsOAuth2ConsentException() {
        var request = new OAuth2ConsentRequest("crm-123", "state", Set.of("open_id"));
        when(httpRequest.getHeader(HttpHeaders.COOKIE)).thenReturn("SSO_SESSION=session-id");
        when(httpRequest.getScheme()).thenReturn("http");
        when(httpRequest.getServerName()).thenReturn("localhost");
        when(httpRequest.getServerPort()).thenReturn(9090);
        when(authorizationServerSettings.getAuthorizationEndpoint()).thenReturn("/oauth2/authorize");
        when(captureRedirectRestTemplate.postForEntity(anyString(), any(), eq(Void.class)))
            .thenThrow(mock(HttpStatusCodeException.class));

        assertThatThrownBy(() -> service.requestAuthorizationRedirect(request, httpRequest))
            .isInstanceOf(OAuth2ConsentException.class)
            .satisfies(ex -> {
                var consentException = (OAuth2ConsentException) ex;
                assertThat(consentException.getCode()).isEqualTo("reject_consent");
            });
    }

    @Test
    void requestAuthorizationRedirect_withNoLocation_throwsOAuth2ConsentException() {
        var request = new OAuth2ConsentRequest("crm-123", "state", Set.of("open_id"));
        when(httpRequest.getHeader(HttpHeaders.COOKIE)).thenReturn("SSO_SESSION=session-id");
        when(httpRequest.getScheme()).thenReturn("http");
        when(httpRequest.getServerName()).thenReturn("localhost");
        when(httpRequest.getServerPort()).thenReturn(9090);
        when(authorizationServerSettings.getAuthorizationEndpoint()).thenReturn("/oauth2/authorize");
        ResponseEntity<Void> oauthResponse = ResponseEntity.ok().build();
        when(captureRedirectRestTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(oauthResponse);

        assertThatThrownBy(() -> service.requestAuthorizationRedirect(request, httpRequest))
            .isInstanceOf(OAuth2ConsentException.class)
            .satisfies(ex -> {
                var consentException = (OAuth2ConsentException) ex;
                assertThat(consentException.getCode()).isEqualTo("redirect_uri_not_found");
            });
    }

    @Test
    void requestAuthorizationRedirect_withCorrectResponse_returnRedirectLocation() {
        var request = new OAuth2ConsentRequest("crm-123", "state", Set.of("open_id"));
        when(httpRequest.getHeader(HttpHeaders.COOKIE)).thenReturn("SSO_SESSION=session-id");
        when(httpRequest.getScheme()).thenReturn("http");
        when(httpRequest.getServerName()).thenReturn("localhost");
        when(httpRequest.getServerPort()).thenReturn(9090);
        when(authorizationServerSettings.getAuthorizationEndpoint()).thenReturn("/oauth2/authorize");
        ResponseEntity<Void> oauthResponse = ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create("http://example.com/oauth/code?scope=open_id"))
            .build();
        when(captureRedirectRestTemplate.postForEntity(anyString(), any(), eq(Void.class))).thenReturn(oauthResponse);

        var result = service.requestAuthorizationRedirect(request, httpRequest);

        assertThat(result).isEqualTo("http://example.com/oauth/code?scope=open_id");
    }
}