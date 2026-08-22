package gr.grodov.grsso.oauth_client.api.dto.validator;

import gr.grodov.grsso.oauth_client.api.dto.validator.annotation.AllowAuthGrantTypes;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AllowAuthGrantTypesValidator implements ConstraintValidator<AllowAuthGrantTypes, Set<OAuthAuthorizationGrantType>> {
    private final OAuthClientPropertiesService oAuthClientPropertiesService;

    @Override
    public boolean isValid(Set<OAuthAuthorizationGrantType> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return oAuthClientPropertiesService.validAuthorizationGrantTypes(value);
    }
}
