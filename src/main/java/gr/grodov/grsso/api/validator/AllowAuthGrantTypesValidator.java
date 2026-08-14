package gr.grodov.grsso.api.validator;

import gr.grodov.grsso.api.validator.annotation.AllowAuthGrantTypes;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.service.OAuthPropertiesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AllowAuthGrantTypesValidator implements ConstraintValidator<AllowAuthGrantTypes, Set<OAuthAuthorizationGrantType>> {
    private final OAuthPropertiesService oAuthPropertiesService;

    @Override
    public boolean isValid(Set<OAuthAuthorizationGrantType> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return oAuthPropertiesService.validAuthorizationGrantTypes(value);
    }
}
