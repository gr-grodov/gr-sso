package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.security.service.SessionService;
import gr.grodov.grsso.service.exceptions.OAuth2ConsentException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class OAuth2FlowService {

    private final String serverPort;
    private final SessionService sessionService;
    private final RestTemplate captureRedirectRestTemplate;
    private final AuthorizationServerSettings authorizationServerSettings;

    private final String ATTRIBUTE_REDIRECT_URI = "OAUTH_REDIRECT_URI";

    public OAuth2FlowService(
        @Value("${server.port}") String serverPort,
        @Qualifier("captureRedirectRestTemplate") RestTemplate captureRedirectRestTemplate,
        SessionService sessionService,
        AuthorizationServerSettings authorizationServerSettings
    ) {
        this.serverPort = serverPort;
        this.sessionService = sessionService;
        this.captureRedirectRestTemplate = captureRedirectRestTemplate;
        this.authorizationServerSettings = authorizationServerSettings;
    }

    public void saveRedirectRequest(String request) {
        sessionService.setAttribute(ATTRIBUTE_REDIRECT_URI, request);
    }

    public String getSavedRedirectRequest() {
        String redirectRequest = sessionService.getAttribute(ATTRIBUTE_REDIRECT_URI, String.class);
        sessionService.removeAttribute(ATTRIBUTE_REDIRECT_URI);
        return redirectRequest;
    }

    public void sendOAuthConsent(OAuth2ConsentRequest consentRequest, HttpServletRequest httpRequest) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", consentRequest.getClientId());
        form.add("state", consentRequest.getState());
        consentRequest.getScopes().forEach(scope -> form.add("scope", scope));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.COOKIE, httpRequest.getHeader(HttpHeaders.COOKIE));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

        ResponseEntity<Void> response;
        try {
            // TODO убрать хардкод
            response = captureRedirectRestTemplate.postForEntity(
                String.format("http://localhost:%s%s", serverPort, authorizationServerSettings.getAuthorizationEndpoint()),
                entity,
                Void.class
            );
        } catch (HttpStatusCodeException ex) {
            throw new OAuth2ConsentException();
        }

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new OAuth2ConsentException();
        }

        saveRedirectRequest(location.toString());
    }
}
