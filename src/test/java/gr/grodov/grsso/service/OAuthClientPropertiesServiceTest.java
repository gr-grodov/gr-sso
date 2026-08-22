package gr.grodov.grsso.service;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.oauth_client.props.OAuthAppProperties;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OAuthClientPropertiesServiceTest {

    private OAuthAppProperties properties;
    private OAuthClientPropertiesService oAuthClientPropertiesService;

    @BeforeEach
    void init() {
        properties = new OAuthAppProperties(
            Set.of("AUTHORIZATION_CODE", "REFRESH_TOKEN"),
            Set.of("CLIENT_SECRET_BASIC", "NONE")
        );
        oAuthClientPropertiesService = new OAuthClientPropertiesService(properties);
    }

    @Nested
    class AuthenticationMethods {

        @Test
        void validAuthenticationMethods_withIncorrectMethods_returnFalse() {
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_JWT,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                OAuthClientAuthenticationMethod.NONE
            );

            assertThat(oAuthClientPropertiesService.validAuthenticationMethods(methods)).isFalse();
        }


        @Test
        void validAuthenticationMethods_withIncludeOnlyCorrectMethods_returnTrue() {
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC
            );

            assertThat(oAuthClientPropertiesService.validAuthenticationMethods(methods)).isTrue();
        }

        @Test
        void filterAuthenticationMethods_withIncorrectMethods_returnMethods() {
            var expectedMethods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            var methods = Set.of(
                OAuthClientAuthenticationMethod.CLIENT_SECRET_JWT,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                OAuthClientAuthenticationMethod.CLIENT_SECRET_POST
            );

            var result = oAuthClientPropertiesService.filterAuthenticationMethods(methods);

            assertThat(result).isEqualTo(expectedMethods);
        }
    }

    @Nested
    class AuthorizationGrantTypes {
        @Test
        void validAuthorizationGrantTypes_withIncludeIncorrectGrantTypes_returnFalse() {
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE,
                OAuthAuthorizationGrantType.DEVICE_CODE
            );

            assertThat(oAuthClientPropertiesService.validAuthorizationGrantTypes(types)).isFalse();
        }

        @Test
        void validAuthorizationGrantTypes_withIncludeOnlyCorrectGrantTypes_returnFalse() {
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE
            );

            assertThat(oAuthClientPropertiesService.validAuthorizationGrantTypes(types)).isTrue();
        }

        @Test
        void filterAuthorizationGrantTypes_withIncorrectTypes_returnMethods() {
            var expectedTypes = Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE);
            var types = Set.of(
                OAuthAuthorizationGrantType.AUTHORIZATION_CODE,
                OAuthAuthorizationGrantType.DEVICE_CODE
            );

            var result = oAuthClientPropertiesService.filterAuthorizationGrantTypes(types);

            assertThat(result).isEqualTo(expectedTypes);
        }
    }

}
