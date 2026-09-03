package gr.grodov.grsso.oauth_client.api.dto.request;

import gr.grodov.grsso.oauth_client.api.dto.validator.AllowAuthGrantTypesValidator;
import gr.grodov.grsso.oauth_client.api.dto.validator.AllowAuthMethodsValidator;
import gr.grodov.grsso.oauth_client.domain.entity.*;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OAuthClientRequestTest.TestConfig.class)
class OAuthClientRequestTest {

    @Autowired
    private Validator validator;

    @MockitoBean
    private OAuthClientPropertiesService oAuthClientPropertiesService;

    @Configuration
    @Import({AllowAuthGrantTypesValidator.class, AllowAuthMethodsValidator.class})
    static class TestConfig {
        @Bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Test
    void validate_withCorrectParameres_returnValidRequest() {
        when(oAuthClientPropertiesService.validAuthorizationGrantTypes(any())).thenReturn(true);
        when(oAuthClientPropertiesService.validAuthenticationMethods(any())).thenReturn(true);
        var request = buildValidRequest();

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void validate_withEmptyClientName_returnInvalidRequest() {
        when(oAuthClientPropertiesService.validAuthorizationGrantTypes(any())).thenReturn(true);
        when(oAuthClientPropertiesService.validAuthenticationMethods(any())).thenReturn(true);
        var request = buildValidRequest();
        request.setClientName(" ");

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("clientName");
    }

    @Test
    void validate_withEmptyRedirectUris_returnInvalidRequest() {
        when(oAuthClientPropertiesService.validAuthorizationGrantTypes(any())).thenReturn(true);
        when(oAuthClientPropertiesService.validAuthenticationMethods(any())).thenReturn(true);
        var request = buildValidRequest();
        request.setRedirectUris(Set.of());

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .anySatisfy(v -> {
                assertThat(v.getPropertyPath().toString()).isEqualTo("redirectUris");
                assertThat(v.getMessage()).isEqualTo("min");
            });
    }

    @Test
    void validate_withIncorrectTypes_returnInvalidRequest() {
        when(oAuthClientPropertiesService.validAuthorizationGrantTypes(any())).thenReturn(false);
        when(oAuthClientPropertiesService.validAuthenticationMethods(any())).thenReturn(true);
        var request = buildValidRequest();

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("authorizationGrantTypes");
    }

    @Test
    void validate_withEmptyTypes_returnInvalidRequest() {
        var request = buildValidRequest();
        request.setAuthorizationGrantTypes(null);

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("authorizationGrantTypes");
    }

    @Test
    void validate_withIncorrectMethods_returnInvalidRequest() {
        when(oAuthClientPropertiesService.validAuthorizationGrantTypes(any())).thenReturn(true);
        when(oAuthClientPropertiesService.validAuthenticationMethods(any())).thenReturn(false);
        var request = buildValidRequest();

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("clientAuthenticationMethods");
    }

    @Test
    void validate_withEmptyMethods_returnInvalidRequest() {
        var request = buildValidRequest();
        request.setAuthorizationGrantTypes(null);

        Set<ConstraintViolation<OAuthClientRequest>> violations = validator.validate(request);

        assertThat(violations)
            .extracting(ConstraintViolation::getPropertyPath)
            .extracting(Object::toString)
            .contains("clientAuthenticationMethods");
    }

    private OAuthClientRequest buildValidRequest() {
        return new OAuthClientRequest(
            null,
            "CRM Client",
            Set.of("http://localhost:8080/login/oauth2/code/grsso"),
            Set.of(OAuthScope.OPEN_ID),
            Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE),
            Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC),
            OAuthClientSettings.builder().build(),
            OAuthTokenSettings.builder().build()
        );
    }
}