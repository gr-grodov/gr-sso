package gr.grodov.grsso.oauth_client.domain.converter;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AuthorizationGrantTypeSetConverterTest {

    private AuthorizationGrantTypeSetConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = new AuthorizationGrantTypeSetConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        var attribute = Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE, OAuthAuthorizationGrantType.DEVICE_CODE);

        var result = converter.convertToDatabaseColumn(attribute);

        assertThat(result).contains("AUTHORIZATION_CODE").contains("DEVICE_CODE");
    }

    @Test
    void convertToEntityAttribute() {
        var dbData = "AUTHORIZATION_CODE,DEVICE_CODE";

        var result = converter.convertToEntityAttribute(dbData);

        assertThat(result).isEqualTo(
            Set.of(OAuthAuthorizationGrantType.AUTHORIZATION_CODE, OAuthAuthorizationGrantType.DEVICE_CODE)
        );
    }

    @Test
    void convertToEntityAttribute_withInvalidDatabaseData_throwsIllegalArgumentException() {
        var dbData = "AUTHORIZATION_CODE,CODE_DEVICE";

        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
            .isInstanceOf(IllegalArgumentException.class);
    }
}