package gr.grodov.grsso.integration.flow_components;

import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientAuthorizeRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientRefreshTokenRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientTokenByCodeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Lazy
@Component
public class ImitationOAuthClientApplication {
    private final RestImitationClient restClient;
    private final String backendURL;

    public ImitationOAuthClientApplication(
        @Autowired RestImitationClient restClient,
        @LocalServerPort String serverPort
    ) {
        this.restClient = restClient;
        this.backendURL = "http://localhost:%s".formatted(serverPort);
    }

    public ResponseEntity<Void> authorize(OAuthClientAuthorizeRequest authorizeRequest) {
        URI authorizeURI = UriComponentsBuilder
            .fromUriString(this.backendURL + "/oauth2/authorize")
            .queryParam("response_type", authorizeRequest.responseType())
            .queryParam("client_id", authorizeRequest.clientID())
            .queryParam("redirect_uri", authorizeRequest.redirectURI())
            .queryParam("scope", String.join(" ", authorizeRequest.scope()))
            .queryParam("state", authorizeRequest.state())
            //.queryParam("nonce", authorizeRequest.nonce())
            .build()
            .toUri();

        return restClient.get(authorizeURI, Void.class);
    }

    public ResponseEntity<Map> getTokenUseClientSecretBasicByCode(OAuthClientTokenByCodeRequest tokenRequest) {
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tokenHeaders.setBasicAuth(tokenRequest.clientId(), tokenRequest.clientSecret());

        MultiValueMap<String, String> tokenForm = new LinkedMultiValueMap<>();
        tokenForm.add("grant_type", "authorization_code");
        tokenForm.add("code", tokenRequest.authorizationCode());
        tokenForm.add("redirect_uri", tokenRequest.redirectURI());

        URI tokenURI = URI.create(this.backendURL + "/oauth2/token");
        HttpEntity<?> request = new HttpEntity<>(tokenForm, tokenHeaders);
        return restClient.exchange(tokenURI, HttpMethod.POST, request, Map.class);
    }

    public ResponseEntity<Map> refreshToken(OAuthClientRefreshTokenRequest refreshTokenRequest) {
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        tokenHeaders.setBasicAuth(refreshTokenRequest.clientId(), refreshTokenRequest.clientSecret());

        MultiValueMap<String, String> tokenForm = new LinkedMultiValueMap<>();
        tokenForm.add("grant_type", "refresh_token");
        tokenForm.add("refresh_token", refreshTokenRequest.refreshToken());

        URI tokenURI = URI.create(this.backendURL + "/oauth2/token");
        HttpEntity<?> request = new HttpEntity<>(tokenForm, tokenHeaders);
        return restClient.exchange(tokenURI, HttpMethod.POST, request, Map.class);
    }

    public void setCookie(ImitationCookie cookie) {
        this.restClient.setCookie(cookie);
    }
}
