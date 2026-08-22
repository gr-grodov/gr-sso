package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.authentication.api.dto.LoginRequest;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class LoginRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectEmailAndPassword_returnValidRequest() {
        var request = new LoginRequest("test@example.com", "Password123!");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "test@",
        "@example.com",
        "test.example.com"
    })
    void validate_withIncorrectEmail_returnInvalidRequest(String email) {
        var request = new LoginRequest(email, "Password123!");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("email") && v.getMessage().equals("invalid")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyEmail_returnInvalidRequest(String email) {
        var request = new LoginRequest(email, "Password123!");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("email") && v.getMessage().equals("empty")
        );
    }

    @Test
    void validate_withIncorrectPassword_returnInvalidRequest() {
        var request = new LoginRequest("test@example.com", "11111111111111111111");

        var violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("password") && v.getMessage().equals("invalid")
        );
    }

    @Test
    void validate_withMiniPassword_returnInvalidRequest() {
        var request = new LoginRequest("test@example.com", "qwerty");

        var violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("password") && v.getMessage().equals("min")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyPassword_returnInvalidRequest(String password) {
        LoginRequest request = new LoginRequest("test@example.com", password);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("password") && v.getMessage().equals("empty")
        );
    }
}