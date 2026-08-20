package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientStatus;
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

class OAuthClientChangeStatusRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectParameters_returnValidRequest() {
        var request = new OAuthClientChangeStatusRequest("1", OAuthClientStatus.ACTIVE);

        Set<ConstraintViolation<OAuthClientChangeStatusRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyID_returnInvalidRequest(String id) {
        var request = new OAuthClientChangeStatusRequest(id, OAuthClientStatus.ACTIVE);

        Set<ConstraintViolation<OAuthClientChangeStatusRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("id") && v.getMessage().equals("empty")
        );
    }

    @Test
    void validate_withEmptyStatus_returnInvalidRequest() {
        var request = new OAuthClientChangeStatusRequest("1", null);

        Set<ConstraintViolation<OAuthClientChangeStatusRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("status") && v.getMessage().equals("empty")
        );
    }
}