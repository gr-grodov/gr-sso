package gr.grodov.grsso.authentication.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RefreshVerifyCodeRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectVerifyIDAndCode_returnValidRequest() {
        var request = new RefreshVerifyCodeRequest("1");

        Set<ConstraintViolation<RefreshVerifyCodeRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyVerifyID_returnInvalidRequest(String verifyId) {
        var request = new RefreshVerifyCodeRequest(verifyId);

        Set<ConstraintViolation<RefreshVerifyCodeRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("verifyId") && v.getMessage().equals("empty")
        );
    }
}