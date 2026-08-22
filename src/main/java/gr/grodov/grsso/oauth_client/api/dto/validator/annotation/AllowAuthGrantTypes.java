package gr.grodov.grsso.oauth_client.api.dto.validator.annotation;

import gr.grodov.grsso.oauth_client.api.dto.validator.AllowAuthGrantTypesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowAuthGrantTypesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowAuthGrantTypes {
    String message() default "invalid";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
