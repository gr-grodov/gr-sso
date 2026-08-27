package gr.grodov.grsso.integration.helpers;

import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.authorization_sso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.authorization_sso.api.dto.response.RedirectURIResponse;
import gr.grodov.grsso.integration.flow_components.ImitationCookie;
import gr.grodov.grsso.integration.flow_components.ImitationFrontendApplication;
import gr.grodov.grsso.integration.flow_components.ImitationOAuthClientApplication;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientAuthorizeRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientRefreshTokenRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientTokenByCodeRequest;
import gr.grodov.grsso.integration.flow_components.dto.response.AuthorizeTokenByCodeResponse;
import gr.grodov.grsso.integration.flow_components.dto.response.RefreshTokenResponse;
import gr.grodov.grsso.integration.helpers.dto.AuthorizeParams;
import gr.grodov.grsso.integration.helpers.dto.ConsentPageParams;
import gr.grodov.grsso.integration.utils.URIParseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuthFlowTestDriver {

    private final ImitationOAuthClientApplication oAuthClientApplication;
    private final ImitationFrontendApplication frontendApplication;
    private final ImitationCookie cookies;

    private String oauthState;

    public OAuthFlowTestDriver(
        ImitationOAuthClientApplication oAuthClientApplication,
        ImitationFrontendApplication frontendApplication
    ) {
        this.oAuthClientApplication = oAuthClientApplication;
        this.frontendApplication = frontendApplication;

        this.cookies = new ImitationCookie();
        this.oAuthClientApplication.setCookie(cookies);
        this.frontendApplication.setCookie(cookies);
    }

    public ResponseEntity<Void> startOAuthAuthorize(OAuthClientAuthorizeRequest authorizeRequest) {
        this.oauthState = authorizeRequest.state();
        return oAuthClientApplication.authorize(authorizeRequest);
    }

    public void refreshCSRFToken() {
        ResponseEntity<DefaultCsrfToken> csrfResponse = frontendApplication.csrf();

        assertThat(csrfResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(csrfResponse.getBody()).isNotNull();

        cookies.addCSRFHeader(csrfResponse.getBody());
    }

    public void loginInSSO(LoginRequest loginRequest) {
        refreshCSRFToken();
        ResponseEntity<?> loginResponse = frontendApplication.login(loginRequest);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    public RedirectURIResponse continueFlow() {
        ResponseEntity<RedirectURIResponse> response = frontendApplication.oauthContinue();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        return response.getBody();
    }

    public ResponseEntity<Void> followFlowRedirect(RedirectURIResponse uriResponse) {
        return frontendApplication.followRedirect(uriResponse);
    }

    public ConsentPageParams extractConsentPageParams(URI redirectConsentFrontendPage) {
        MultiValueMap<String, String> params = URIParseUtils.getQueryParams(redirectConsentFrontendPage);
        return new ConsentPageParams(params.getFirst("state"), params.getFirst("client_id"), params.getFirst("scope"));
    }

    public ResponseEntity<?> submitConsent(OAuth2ConsentRequest consentRequest) {
        return frontendApplication.oauthConsent(consentRequest);
    }

    public AuthorizeParams extractAuthorizeParams(String authorizeRequest) {
        MultiValueMap<String, String> params = URIParseUtils.getQueryParams(URI.create(authorizeRequest));
        return new AuthorizeParams(params.getFirst("code"), params.getFirst("state"));
    }

    public AuthorizeParams runFullFlowGetAuthorizeCode(OAuthClientAuthorizeRequest authorizeRequest, LoginRequest loginRequest) {
        startOAuthAuthorize(authorizeRequest);

        refreshCSRFToken();
        loginInSSO(loginRequest);

        RedirectURIResponse afterLogin = continueFlow();
        ResponseEntity<Void> consentPageRedirect = followFlowRedirect(afterLogin);
        ConsentPageParams consentParams = extractConsentPageParams(consentPageRedirect.getHeaders().getLocation());

        submitConsent(new OAuth2ConsentRequest(consentParams.clientId(), consentParams.state(), Set.of("openid", "profile")));

        RedirectURIResponse afterConsent = continueFlow();
        AuthorizeParams authorizeParams = extractAuthorizeParams(afterConsent.redirectURI());

        assertThat(authorizeParams.state()).isEqualTo(oauthState);
        return authorizeParams;
    }

    public AuthorizeTokenByCodeResponse authorizeTokenByCode(OAuthClientTokenByCodeRequest codeRequest) {
        ResponseEntity<Map> tokenResponse = oAuthClientApplication.getTokenUseClientSecretBasicByCode(codeRequest);
        Map<String, String> tokenBody = tokenResponse.getBody();

        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenBody).isNotNull();
        return new AuthorizeTokenByCodeResponse(
            tokenBody.get("access_token"),
            tokenBody.get("refresh_token"),
            tokenBody.get("token_type"),
            tokenBody.get("id_token"),
            tokenBody.get("scope")
        );
    }

    public RefreshTokenResponse refreshToken(OAuthClientRefreshTokenRequest tokenRequest) {
        ResponseEntity<Map> tokenResponse = oAuthClientApplication.refreshToken(tokenRequest);
        Map<String, String> tokenBody = tokenResponse.getBody();

        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenBody).isNotNull();
        return new RefreshTokenResponse(
            tokenBody.get("access_token"),
            tokenBody.get("refresh_token"),
            tokenBody.get("token_type"),
            tokenBody.get("id_token"),
            tokenBody.get("scope")
        );
    }
}
