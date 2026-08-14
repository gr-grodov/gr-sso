package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.service.exceptions.OAuth2ConsentException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ConsentService {

    private final RestTemplate captureRedirectRestTemplate;
    private final AuthorizationServerSettings authorizationServerSettings;

    public String requestAuthorizationRedirect(OAuth2ConsentRequest consentRequest, HttpServletRequest httpRequest) {
        String cookieHeader = httpRequest.getHeader(HttpHeaders.COOKIE);
        if (!StringUtils.hasText(cookieHeader)) {
            throw new OAuth2ConsentException("missing_cookie");
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(
            buildConsentForm(consentRequest),
            buildForwardedHeaders(cookieHeader)
        );

        ResponseEntity<Void> response;
        try {
            response = captureRedirectRestTemplate.postForEntity(buildLoopbackAuthorizationUri(httpRequest), entity, Void.class);
        } catch (HttpStatusCodeException ex) {
            throw new OAuth2ConsentException("reject_consent");
        }

        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new OAuth2ConsentException("redirect_uri_not_found");
        }

        return location.toString();
    }

    private MultiValueMap<String, String> buildConsentForm(OAuth2ConsentRequest consentRequest) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add(OAuth2ParameterNames.CLIENT_ID, consentRequest.getClientId());
        form.add(OAuth2ParameterNames.STATE, consentRequest.getState());
        consentRequest.getScopes().forEach(scope -> form.add(OAuth2ParameterNames.SCOPE, scope));

        return form;
    }

    private HttpHeaders buildForwardedHeaders(String cookieHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add(HttpHeaders.COOKIE, cookieHeader);
        return headers;
    }

    private String buildLoopbackAuthorizationUri(HttpServletRequest httpRequest) {
        return UriComponentsBuilder.newInstance()
            .scheme(httpRequest.getScheme())
            .host(httpRequest.getServerName())
            .port(httpRequest.getServerPort())
            .path(authorizationServerSettings.getAuthorizationEndpoint())
            .build()
            .toUriString();
    }
}