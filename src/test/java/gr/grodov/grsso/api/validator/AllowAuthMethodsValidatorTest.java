package gr.grodov.grsso.api.validator;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.service.OAuthPropertiesService;
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
    private OAuthPropertiesService oAuthPropertiesService;
    @InjectMocks
    private AllowAuthMethodsValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validator_withoutValue_returnFalse() {
        var result = validator.isValid(null, context);

        assertThat(result).isFalse();

        verifyNoInteractions(oAuthPropertiesService);
    }

    @Test
    void validator_withCorrectTypes_returnTrue() {
        var methods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        when(oAuthPropertiesService.validAuthenticationMethods(methods)).thenReturn(true);

        var result = validator.isValid(methods, context);

        assertThat(result).isTrue();
        verify(oAuthPropertiesService).validAuthenticationMethods(methods);
    }

    @Test
    void validator_withIncorrectTypes_returnFalse() {
        var methods = Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        when(oAuthPropertiesService.validAuthenticationMethods(methods)).thenReturn(false);

        var result = validator.isValid(methods, context);

        assertThat(result).isFalse();
        verify(oAuthPropertiesService).validAuthenticationMethods(methods);
    }
}