package gr.grodov.grsso.oauth_client.api.dto.validator;

import gr.grodov.grsso.oauth_client.api.dto.validator.annotation.AllowAuthMethods;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AllowAuthMethodsValidator implements ConstraintValidator<AllowAuthMethods, Set<OAuthClientAuthenticationMethod>> {
    private final OAuthClientPropertiesService oAuthClientPropertiesService;

    @Override
    public boolean isValid(Set<OAuthClientAuthenticationMethod> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return oAuthClientPropertiesService.validAuthenticationMethods(value);
    }
}
