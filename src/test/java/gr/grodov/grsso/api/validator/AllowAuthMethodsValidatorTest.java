package gr.grodov.grsso.api.validator;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.oauth_client.api.dto.validator.AllowAuthMethodsValidator;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AllowAuthMethodsValidatorTest {

    @Mock
    private OAuthClientPropertiesService oAuthClientPropertiesService;
    @InjectMocks
    private AllowAuthMethodsValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validator_withoutValue_returnFalse() {
        var result = validator.isValid(null, context);

        assertThat(result).isFalse();

        verifyNoInteractions(oAuthClientPropertiesService);
    }

    @Test
    void validator_withCorrectTypes_returnTrue() {
        var methods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        when(oAuthClientPropertiesService.validAuthenticationMethods(methods)).thenReturn(true);

        var result = validator.isValid(methods, context);

        assertThat(result).isTrue();
        verify(oAuthClientPropertiesService).validAuthenticationMethods(methods);
    }

    @Test
    void validator_withIncorrectTypes_returnFalse() {
        var methods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        when(oAuthClientPropertiesService.validAuthenticationMethods(methods)).thenReturn(false);

        var result = validator.isValid(methods, context);

        assertThat(result).isFalse();
        verify(oAuthClientPropertiesService).validAuthenticationMethods(methods);
    }
}