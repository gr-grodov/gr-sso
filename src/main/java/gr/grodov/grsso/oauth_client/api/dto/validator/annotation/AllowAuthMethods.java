package gr.grodov.grsso.oauth_client.api.dto.validator.annotation;

import gr.grodov.grsso.oauth_client.api.dto.validator.AllowAuthMethodsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowAuthMethodsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowAuthMethods {
    String message() default "invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
