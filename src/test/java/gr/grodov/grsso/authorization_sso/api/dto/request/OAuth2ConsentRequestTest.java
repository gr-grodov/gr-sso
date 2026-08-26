package gr.grodov.grsso.authorization_sso.api.dto.request;

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

class OAuth2ConsentRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectParameters_returnValidRequest() {
        var request = new OAuth2ConsentRequest("client-id", "state", Set.of("openid"));

        Set<ConstraintViolation<OAuth2ConsentRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyClientID_returnInvalidRequest(String clientID) {
        var request = new OAuth2ConsentRequest(clientID, "state", Set.of("openid"));

        Set<ConstraintViolation<OAuth2ConsentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("clientId") && v.getMessage().equals("empty")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    void validate_withEmptyState_returnInvalidRequest(String state) {
        var request = new OAuth2ConsentRequest("client-id", state, Set.of("openid"));

        Set<ConstraintViolation<OAuth2ConsentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("state") && v.getMessage().equals("empty")
        );
    }

    @Test
    void validate_withEmptyScope_returnInvalidRequest() {
        var request = new OAuth2ConsentRequest("client-id", "state", Set.of());

        Set<ConstraintViolation<OAuth2ConsentRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("scopes") && v.getMessage().equals("min")
        );
    }
}