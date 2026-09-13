package gr.grodov.grsso.oauth_session.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class DeleteOAuth2SessionRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectParameters_returnValidRequest() {
        var request = new DeleteOAuth2SessionRequest("1", UUID.randomUUID());

        Set<ConstraintViolation<DeleteOAuth2SessionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyClientId_returnInvalidRequest(String clientId) {
        var request = new DeleteOAuth2SessionRequest(clientId, UUID.randomUUID());

        Set<ConstraintViolation<DeleteOAuth2SessionRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("clientId") && v.getMessage().equals("empty")
        );
    }

    @Test
    void validate_withEmptyStatus_returnInvalidRequest() {
        var request = new DeleteOAuth2SessionRequest("1", null);

        Set<ConstraintViolation<DeleteOAuth2SessionRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("userId") && v.getMessage().equals("empty")
        );
    }
}