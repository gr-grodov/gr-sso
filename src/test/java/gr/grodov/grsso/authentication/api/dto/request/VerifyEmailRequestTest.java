package gr.grodov.grsso.authentication.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class VerifyEmailRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectVerifyIDAndCode_returnValidRequest() {
        var request = new VerifyEmailRequest("1", "123456");

        Set<ConstraintViolation<VerifyEmailRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "12",
        "qwerty",
        "123asd",
        "12345!"
    })
    void validate_withIncorrectVerifyCode_returnInvalidRequest(String verifyCode) {
        var request = new VerifyEmailRequest("1", verifyCode);

        Set<ConstraintViolation<VerifyEmailRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("verifyCode") && v.getMessage().equals("invalid")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyVerifyCode_returnInvalidRequest(String verifyCode) {
        var request = new VerifyEmailRequest("1", verifyCode);

        Set<ConstraintViolation<VerifyEmailRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("verifyCode") && v.getMessage().equals("empty")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyVerifyID_returnInvalidRequest(String verifyID) {
        var request = new VerifyEmailRequest(verifyID, "123456");

        Set<ConstraintViolation<VerifyEmailRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("verifyId") && v.getMessage().equals("empty")
        );
    }
}