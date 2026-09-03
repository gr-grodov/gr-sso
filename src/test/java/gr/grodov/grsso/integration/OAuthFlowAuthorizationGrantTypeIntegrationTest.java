package gr.grodov.grsso.integration;

import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.authorization_sso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.oauth_client.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.authorization_sso.api.dto.response.RedirectURIResponse;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.integration.flow_components.ImitationFrontendApplication;
import gr.grodov.grsso.integration.flow_components.ImitationOAuthClientApplication;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientAuthorizeRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientRefreshTokenRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientTokenByCodeRequest;
import gr.grodov.grsso.integration.flow_components.dto.response.AuthorizeTokenByCodeResponse;
import gr.grodov.grsso.integration.flow_components.dto.response.RefreshTokenResponse;
import gr.grodov.grsso.integration.helpers.DatabaseCleaner;
import gr.grodov.grsso.integration.helpers.OAuthFlowTestDriver;
import gr.grodov.grsso.integration.helpers.dto.AuthorizeParams;
import gr.grodov.grsso.integration.helpers.dto.ConsentPageParams;
import gr.grodov.grsso.common.props.FrontendAppProperties;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("""
    Цепочка вызовов при OAuth (OAuthAuthorizationGrantType.AUTHORIZATION_CODE)
    1) Пользователь заходит на OAuth клиента
    2) Пользователь аутентифицируется через SSO (посылает запрос http://crm-client/oauth2/authorization/grsso)
    3) Приходит "302" и его редиректит на SSO (http://sso/oauth2/authorize)
    4) Так как пользователь не аутентифицирован, то его редиректит "302" на страницу логина на фронте (http://sso-front/login)
    5) Фронтенд посылает запрос для получения csrf токена (http://sso/oauth2/authorize)
    5) Пользователь вводит данные и отправляет LoginRequest на http://sso/api/auth/login
    6) Фронт отправляет запрос http://sso/api/oauth2/continue, если фронту приходит ответ с redirectURI, то переходит на redirectURI (http://sso/oauth2/authorize)
    7) Если согласия (Scope) не были даны, то редиректим на фронт http://sso-front/oauth2/consent?scope=&state=&client_id=
    8) Фронт отправляет запрос с разрешенными Scope на http://sso/api/oauth2/consent
    9) Фронт отправляет запрос http://sso/api/oauth2/continue, если фронту приходит ответ с redirectURI, то переходит на redirectURI (http://sso/oauth2/authorize)
    10) OAuth клиент обменивает Authorization Code на access_token
    """)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OAuthFlowAuthorizationGrantTypeIntegrationTest extends AbstractIntegrationTest{

    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "Password123!";
    private static final String OAUTH_CLIENT_RESPONSE_TYPE = "code";
    private static final String OAUTH_CLIENT_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/grsso";
    private static final List<String> OAUTH_CLIENT_SCOPES = List.of(OAuthScope.OPEN_ID.getScopeValue(), OAuthScope.PROFILE.getScopeValue());
    private static final String OAUTH_CLIENT_AUTHORIZE_STATE = "STATE-EXAMPLE";

    @Autowired
    private OAuthClientsService oAuthClientsService;
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ImitationOAuthClientApplication oAuthClientApplication;
    @Autowired
    private ImitationFrontendApplication frontendApplication;
    @Autowired
    private FrontendAppProperties frontendAppProperties;

    private OAuthClientSecretInfo clientSecretInfo;
    private OAuthFlowTestDriver flowTestDriver;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        var clientRequest = new OAuthClientRequest(
            null,
            "CRM Application",
            Set.of(OAUTH_CLIENT_REDIRECT_URI),
            Set.of(OAuthScope.OPEN_ID, OAuthScope.PROFILE),
            Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE, OAuthAuthorizationGrantType.REFRESH_TOKEN),
            Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC),
            OAuthClientSettings.builder().build(),
            OAuthTokenSettings.builder().build()
        );
        OAuthClientSecretInfoResponse clientInfo = oAuthClientsService.save(clientRequest);
        this.clientSecretInfo = new OAuthClientSecretInfo(clientInfo.clientID(), clientInfo.clientSecret());

        var user = userInfoService.createNewUser(TEST_EMAIL, TEST_PASSWORD, AuthProvider.LOCAL);
        userInfoService.enabledUserInfo(user.id(), true);

        this.flowTestDriver = new OAuthFlowTestDriver(oAuthClientApplication, frontendApplication);
    }

    @AfterEach
    void cleanUp() {
        databaseCleaner.cleanAll();
    }

    @Test
    @DisplayName("Неаутентифицированный пользователь редиректиться на фронтенд страницу логина")
    void unauthenticatedUser_redirectToFrontendLoginPage() {
        ResponseEntity<Void> authorizeResponse = flowTestDriver.startOAuthAuthorize(new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        ));
        URI loginPage = authorizeResponse.getHeaders().getLocation();

        assertThat(authorizeResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(loginPage).isNotNull();
        assertThat(loginPage.toString()).isEqualTo(frontendAppProperties.loginUrl());
    }

    @Test
    @DisplayName("После успешного логина '/api/oauth2/continue' возвращает oauth authorize URI")
    void afterLogin_getAuthorizeRequest() {
        flowTestDriver.startOAuthAuthorize(new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        ));
        flowTestDriver.loginInSSO(new LoginRequest(TEST_EMAIL, TEST_PASSWORD));
        RedirectURIResponse afterLoginRedirect = flowTestDriver.continueFlow();

        assertThat(afterLoginRedirect.redirectURI()).contains("/oauth2/authorize");
    }

    @Test
    @DisplayName("После успешного логина, возвращенный oauth authorize URI редиректит пользователя на страницу подтверждения соглашений")
    void afterLoginAuthorize_redirectToConsent() {
        flowTestDriver.startOAuthAuthorize(new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        ));
        flowTestDriver.loginInSSO(new LoginRequest(TEST_EMAIL, TEST_PASSWORD));
        RedirectURIResponse afterLoginRedirect = flowTestDriver.continueFlow();
        ResponseEntity<Void> redirectToConsent = flowTestDriver.followFlowRedirect(afterLoginRedirect);
        URI consentPage = redirectToConsent.getHeaders().getLocation();

        assertThat(redirectToConsent.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(consentPage).isNotNull();
        assertThat(consentPage.toString()).startsWith(frontendAppProperties.oauthConsentUrl());
        assertThat(consentPage.getQuery())
            .contains("client_id")
            .contains("state")
            .contains("scope");
    }

    @Test
    @DisplayName("После подтверждения разрешений '/api/oauth2/continue' возвращает oauth authorize URI")
    void afterConsent_getAuthorizeRequest() {
        flowTestDriver.startOAuthAuthorize(new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        ));
        flowTestDriver.loginInSSO(new LoginRequest(TEST_EMAIL, TEST_PASSWORD));
        RedirectURIResponse afterLoginRedirect = flowTestDriver.continueFlow();
        ResponseEntity<Void> redirectToConsent = flowTestDriver.followFlowRedirect(afterLoginRedirect);
        ConsentPageParams params = flowTestDriver.extractConsentPageParams(redirectToConsent.getHeaders().getLocation());
        flowTestDriver.submitConsent(new OAuth2ConsentRequest(params.clientId(), params.state(), new HashSet<>(OAUTH_CLIENT_SCOPES)));
        RedirectURIResponse afterConsent = flowTestDriver.continueFlow();
        AuthorizeParams authorizeParams = flowTestDriver.extractAuthorizeParams(afterConsent.redirectURI());

        assertThat(afterConsent.redirectURI()).startsWith(OAUTH_CLIENT_REDIRECT_URI);
        assertThat(authorizeParams.state()).isNotNull();
        assertThat(authorizeParams.code()).isNotNull();
    }

    @Test
    @DisplayName("После логина и подтверждения разрешений получаем токены")
    void afterFullFlow_returnGetTokens() {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);

        assertThat(codeResponse.accessToken()).isNotNull();
        assertThat(codeResponse.refreshToken()).isNotNull();
        assertThat(codeResponse.idToken()).isNotNull();
        assertThat(codeResponse.tokenType()).isEqualTo("Bearer");
        assertThat(codeResponse.scope()).contains(OAUTH_CLIENT_SCOPES);
    }

    @Test
    @DisplayName("После логина и подтверждения разрешений получаем токены")
    void afterFullFlow_ifRepeatAuthorizeToken_throwsHttpClientErrorException() {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        flowTestDriver.authorizeTokenByCode(codeRequest);
        HttpClientErrorException exception = assertThrows(
            HttpClientErrorException.class,
            () -> flowTestDriver.authorizeTokenByCode(codeRequest)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("После аутентификации OAuth-клиента обновляем токены")
    void afterFullFlow_refreshTokens() {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);
        RefreshTokenResponse tokenResponse = flowTestDriver.refreshToken(new OAuthClientRefreshTokenRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, codeResponse.refreshToken()
        ));

        assertThat(tokenResponse.accessToken()).isNotNull();
        assertThat(tokenResponse.refreshToken()).isNotNull();
        assertThat(tokenResponse.idToken()).isNotNull();
        assertThat(tokenResponse.tokenType()).isEqualTo("Bearer");
        assertThat(tokenResponse.scope()).contains(OAUTH_CLIENT_SCOPES);
    }

    @Test
    @DisplayName("После аутентификации OAuth-клиента обновляем токены")
    void afterFullFlow_repeatRefreshTokens() {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);
        RefreshTokenResponse tokenResponse = flowTestDriver.refreshToken(new OAuthClientRefreshTokenRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, codeResponse.refreshToken()
        ));
        RefreshTokenResponse repeatTokenResponse = flowTestDriver.refreshToken(new OAuthClientRefreshTokenRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, tokenResponse.refreshToken()
        ));

        assertThat(repeatTokenResponse.accessToken()).isNotNull();
        assertThat(repeatTokenResponse.refreshToken()).isNotNull();
        assertThat(repeatTokenResponse.idToken()).isNotNull();
        assertThat(repeatTokenResponse.tokenType()).isEqualTo("Bearer");
        assertThat(repeatTokenResponse.scope()).contains(OAUTH_CLIENT_SCOPES);
    }

    @Test
    @DisplayName("После аутентификации OAuth-клиента обновляем токены")
    void afterFullFlow_repeatRefreshTokensWithOldToken_throwsHttpClientErrorException() {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId, OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);
        flowTestDriver.refreshToken(new OAuthClientRefreshTokenRequest(
            clientSecretInfo.clientId, clientSecretInfo.clientSecret, codeResponse.refreshToken()
        ));
        HttpClientErrorException exception = assertThrows(
            HttpClientErrorException.class,
            () -> {
                flowTestDriver.refreshToken(new OAuthClientRefreshTokenRequest(
                    clientSecretInfo.clientId, clientSecretInfo.clientSecret, codeResponse.refreshToken()
                ));
            }
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    record  OAuthClientSecretInfo (
        String clientId,
        String clientSecret
    ) {}
}