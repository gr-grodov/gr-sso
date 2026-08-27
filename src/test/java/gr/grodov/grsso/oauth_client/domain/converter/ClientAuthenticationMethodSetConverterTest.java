package gr.grodov.grsso.oauth_client.domain.converter;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ClientAuthenticationMethodSetConverterTest {

    private ClientAuthenticationMethodSetConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = new ClientAuthenticationMethodSetConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        var attribute = Set.of(OAuthClientAuthenticationMethod.NONE, OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        var result = converter.convertToDatabaseColumn(attribute);

        assertThat(result).contains("NONE").contains("CLIENT_SECRET_BASIC");
    }

    @Test
    void convertToEntityAttribute() {
        var dbData = "NONE,CLIENT_SECRET_BASIC";

        var result = converter.convertToEntityAttribute(dbData);

        assertThat(result).isEqualTo(
            Set.of(OAuthClientAuthenticationMethod.NONE, OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        );
    }

    @Test
    void convertToEntityAttribute_withInvalidDatabaseData_throwsIllegalArgumentException() {
        var dbData = "NONE,CLIENT_SECRET";

        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
            .isInstanceOf(IllegalArgumentException.class);
    }
}