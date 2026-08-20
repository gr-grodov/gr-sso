package gr.grodov.grsso.api.validator;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.service.OAuthPropertiesService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AllowAuthGrantTypesValidatorTest {

    @Mock
    private OAuthPropertiesService oAuthPropertiesService;
    @InjectMocks
    private AllowAuthGrantTypesValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void validator_withoutValue_returnFalse() {
        boolean result = validator.isValid(null, context);

        assertThat(result).isFalse();

        verifyNoInteractions(oAuthPropertiesService);
    }

    @Test
    void validator_withCorrectTypes_returnTrue() {
        var grantTypes = Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE);
        when(oAuthPropertiesService.validAuthorizationGrantTypes(grantTypes)).thenReturn(true);

        var result = validator.isValid(grantTypes, context);

        assertThat(result).isTrue();
        verify(oAuthPropertiesService).validAuthorizationGrantTypes(grantTypes);
    }

    @Test
    void validator_withIncorrectTypes_returnFalse() {
        var grantTypes = Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE);
        when(oAuthPropertiesService.validAuthorizationGrantTypes(grantTypes)).thenReturn(false);

        var result = validator.isValid(grantTypes, context);

        assertThat(result).isFalse();
        verify(oAuthPropertiesService).validAuthorizationGrantTypes(grantTypes);
    }
}