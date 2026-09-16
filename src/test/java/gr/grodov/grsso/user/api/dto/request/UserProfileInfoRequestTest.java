package gr.grodov.grsso.user.api.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserProfileInfoRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validate_withCorrectProfileData_returnValidRequest() {
        var request = new UserProfileInfoRequest("Ivan", "Ivanov", "Ivanovich", UUID.randomUUID());

        Set<ConstraintViolation<UserProfileInfoRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Ivan!",
        "Ivan123",
        " Ivan",
        "Ivan "
    })
    void validate_withIncorrectFirstName_returnInvalidRequest(String firstName) {
        var request = new UserProfileInfoRequest(firstName, "Ivanov", "Ivanovich", UUID.randomUUID());

        Set<ConstraintViolation<UserProfileInfoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("firstName") && v.getMessage().equals("invalid")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Ivanov!",
        "Ivanov123",
        " Ivanov",
        "Ivanov "
    })
    void validate_withIncorrectLastName_returnInvalidRequest(String lastName) {
        var request = new UserProfileInfoRequest("Ivan", lastName, "Ivanovich", UUID.randomUUID());

        Set<ConstraintViolation<UserProfileInfoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("lastName") && v.getMessage().equals("invalid")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "Ivanovich!",
        "Ivanovich123",
        " Ivanovich",
        "Ivanovich "
    })
    void validate_withIncorrectPatronymic_returnInvalidRequest(String patronymic) {
        var request = new UserProfileInfoRequest("Ivan", "Ivanov", patronymic, UUID.randomUUID());

        Set<ConstraintViolation<UserProfileInfoRequest>> violations = validator.validate(request);

        assertThat(violations).anyMatch(v ->
            v.getPropertyPath().toString().equals("patronymic") && v.getMessage().equals("invalid")
        );
    }
}