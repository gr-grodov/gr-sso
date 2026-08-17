package gr.grodov.grsso.service;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.props.OAuthAppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OAuthPropertiesServiceTest {

    private OAuthAppProperties properties;
    private OAuthPropertiesService oAuthPropertiesService;

    @BeforeEach
    void init() {
        properties = new OAuthAppProperties(
            Set.of("AUTHORIZATION_CODE", "REFRESH_TOKEN"),
            Set.of("CLIENT_SECRET_BASIC", "NONE")
        );
        oAuthPropertiesService = new OAuthPropertiesService(properties);
    }

    @Nested
    class AuthenticationMethods {

        @Test
        void validAuthenticationMethods_IncludeIncorrectMethods() {
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_JWT,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                OAuthClientAuthenticationMethod.NONE
            );

            assertFalse(oAuthPropertiesService.validAuthenticationMethods(methods));
        }


        @Test
        void validAuthenticationMethods_IncludeOnlyCorrectMethods() {
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC
            );

            assertTrue(oAuthPropertiesService.validAuthenticationMethods(methods));
        }

        @Test
        void filterAuthenticationMethods_WithIncorrectMethods() {
            var expectedMethods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_JWT,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_POST
            );

            var result = oAuthPropertiesService.filterAuthenticationMethods(methods);

            assertEquals(expectedMethods, result);
        }
    }

    @Nested
    class AuthorizationGrantTypes {
        @Test
        void validAuthorizationGrantTypes_IncludeIncorrectGrantTypes() {
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE,
                OAuthAuthorizationGrantType.DEVICE_CODE
            );

            assertFalse(oAuthPropertiesService.validAuthorizationGrantTypes(types));
        }

        @Test
        void validAuthorizationGrantTypes_IncludeOnlyCorrectGrantTypes() {
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE
            );

            assertTrue(oAuthPropertiesService.validAuthorizationGrantTypes(types));
        }

        void filterAuthorizationGrantTypes_WithIncorrectTypes() {
            var expectedTypes = Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE);
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE,
                OAuthAuthorizationGrantType.DEVICE_CODE
            );

            var result = oAuthPropertiesService.filterAuthorizationGrantTypes(types);

            assertEquals(expectedTypes, result);
        }
    }

}
