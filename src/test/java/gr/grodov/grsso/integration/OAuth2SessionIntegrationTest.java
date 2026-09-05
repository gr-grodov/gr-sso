package gr.grodov.grsso.integration;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.integration.flow_components.ImitationFrontendApplication;
import gr.grodov.grsso.integration.flow_components.ImitationOAuthClientApplication;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientAuthorizeRequest;
import gr.grodov.grsso.integration.flow_components.dto.request.OAuthClientTokenByCodeRequest;
import gr.grodov.grsso.integration.flow_components.dto.response.AuthorizeTokenByCodeResponse;
import gr.grodov.grsso.integration.helpers.DatabaseCleaner;
import gr.grodov.grsso.integration.helpers.OAuthFlowTestDriver;
import gr.grodov.grsso.integration.helpers.dto.AuthorizeParams;
import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.oauth_client.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.session_sso.domain.entity.OAuth2Session;
import gr.grodov.grsso.session_sso.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.client.oidc.authentication.logout.LogoutTokenClaimNames;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("Проверяет создание OAuth2Session и обновлении OAuth2Authorization")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OAuth2SessionIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private OAuth2SessionRepo oAuth2SessionRepo;
    @Autowired
    private OAuthClientsService oAuthClientsService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ImitationOAuthClientApplication oAuthClientApplication;
    @Autowired
    private ImitationFrontendApplication frontendApplication;

    private OAuthFlowAuthorizationGrantTypeIntegrationTest.OAuthClientSecretInfo clientSecretInfo;
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
        this.clientSecretInfo = new OAuthFlowAuthorizationGrantTypeIntegrationTest.OAuthClientSecretInfo(clientInfo.clientID(), clientInfo.clientSecret());

        var user = userInfoService.createNewUser(TEST_EMAIL, TEST_PASSWORD, AuthProvider.LOCAL);
        userInfoService.enabledUserInfo(user.id(), true);

        this.flowTestDriver = new OAuthFlowTestDriver(oAuthClientApplication, frontendApplication);
    }

    @AfterEach
    void cleanUp() {
        databaseCleaner.cleanAll();
    }

    @Test
    @DisplayName("После получение токенов id_token содержит sid")
    void afterFullFlow_returnGetIdTokenWithSID() throws ParseException {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId(), OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId(), clientSecretInfo.clientSecret(), authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);

        assertThat(codeResponse.idToken()).isNotNull();
        JWTClaimsSet idTokenClaims = JWTParser.parse(codeResponse.idToken()).getJWTClaimsSet();
        assertThat(idTokenClaims.getClaim(LogoutTokenClaimNames.SID)).isNotNull();
        assertThat(idTokenClaims.getSubject()).isNotNull();
    }

    @Test
    @DisplayName("После получение токенов id_token содержит sid")
    void afterFullFlow_createOAuth2Session() throws ParseException {
        var authorizeRequest = new OAuthClientAuthorizeRequest(
            OAUTH_CLIENT_RESPONSE_TYPE, clientSecretInfo.clientId(), OAUTH_CLIENT_REDIRECT_URI, OAUTH_CLIENT_SCOPES, OAUTH_CLIENT_AUTHORIZE_STATE
        );
        var loginRequest = new LoginRequest(TEST_EMAIL, TEST_PASSWORD);
        AuthorizeParams authorizeParams = flowTestDriver.runFullFlowGetAuthorizeCode(authorizeRequest, loginRequest);
        OAuthClientTokenByCodeRequest codeRequest = new OAuthClientTokenByCodeRequest(
            clientSecretInfo.clientId(), clientSecretInfo.clientSecret(), authorizeParams.code(), OAUTH_CLIENT_REDIRECT_URI
        );
        AuthorizeTokenByCodeResponse codeResponse = flowTestDriver.authorizeTokenByCode(codeRequest);
        JWTClaimsSet idTokenClaims = JWTParser.parse(codeResponse.idToken()).getJWTClaimsSet();

        Optional<OAuth2Session> session = oAuth2SessionRepo.findById(UUID.fromString(idTokenClaims.getClaim(LogoutTokenClaimNames.SID).toString()));
        assertThat(session.isPresent()).isTrue();
    }
}
