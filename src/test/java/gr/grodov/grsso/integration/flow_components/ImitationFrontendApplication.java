package gr.grodov.grsso.integration.flow_components;

import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.authorization_sso.api.dto.OAuth2ConsentRequest;
import gr.grodov.grsso.authorization_sso.api.dto.RedirectURIResponse;
import gr.grodov.grsso.common.api.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Component;

import java.net.URI;

@Lazy
@Component
public class ImitationFrontendApplication {

    private final RestImitationClient restClient;
    private final String backendURL;

    public ImitationFrontendApplication(
        @Autowired RestImitationClient restClient,
        @LocalServerPort String serverPort
    ) {
        this.restClient = restClient;
        this.backendURL = "http://localhost:%s".formatted(serverPort);
    }

    public ResponseEntity<DefaultCsrfToken> csrf() {
        URI csrfURI = URI.create(this.backendURL + "/api/config/csrf");
        return restClient.get(csrfURI, DefaultCsrfToken.class);
    }

    public ResponseEntity<SuccessResponse> login(LoginRequest loginRequest) {
        URI loginURI = URI.create(this.backendURL + "/api/auth/login");
        return restClient.post(loginURI, loginRequest, SuccessResponse.class);
    }

    public ResponseEntity<SuccessResponse> oauthConsent(OAuth2ConsentRequest consentRequest) {
        URI consentURI = URI.create(this.backendURL + "/api/oauth2/consent");
        return restClient.post(consentURI, consentRequest, SuccessResponse.class);
    }

    public ResponseEntity<RedirectURIResponse> oauthContinue() {
        URI continueURI = URI.create(this.backendURL + "/api/oauth2/continue");
        return restClient.get(continueURI, RedirectURIResponse.class);
    }

    public ResponseEntity<Void> followRedirect(RedirectURIResponse uriResponse) {
        URI followRedirectURI = URI.create(uriResponse.redirectURI());
        return restClient.get(followRedirectURI, Void.class);
    }

    public void setCookie(ImitationCookie cookie) {
        this.restClient.setCookie(cookie);
    }
}
