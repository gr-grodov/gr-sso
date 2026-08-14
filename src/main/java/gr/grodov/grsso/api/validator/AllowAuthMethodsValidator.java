package gr.grodov.grsso.api.validator;

import gr.grodov.grsso.api.validator.annotation.AllowAuthGrantTypes;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.service.OAuthPropertiesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AllowAuthMethodsValidator implements ConstraintValidator<AllowAuthGrantTypes, Set<OAuthClientAuthenticationMethod>> {
    private final OAuthPropertiesService oAuthPropertiesService;

    @Override
    public boolean isValid(Set<OAuthClientAuthenticationMethod> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return oAuthPropertiesService.validAuthenticationMethods(value);
    }
}
