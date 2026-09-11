package gr.grodov.grsso.integration;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

public class AbstractIntegrationTest {
    protected static final String TEST_EMAIL = "user@example.com";
    protected static final String TEST_PASSWORD = "Password123!";
    protected static final String OAUTH_CLIENT_RESPONSE_TYPE = "code";
    protected static final String OAUTH_CLIENT_REDIRECT_URI = "http://localhost:8080/login/oauth2/code/grsso";
    protected static final List<String> OAUTH_CLIENT_SCOPES = List.of(OAuthScope.OPEN_ID.getScopeValue(), OAuthScope.PROFILE.getScopeValue());
    protected static final String OAUTH_CLIENT_AUTHORIZE_STATE = "STATE-EXAMPLE";

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:18");

    static {
        POSTGRES_CONTAINER.start();
    }

    record OAuthClientSecretInfo (
        String clientId,
        String clientSecret
    ) {}
}
