package gr.grodov.grsso.oauth_client.domain.converter;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import gr.grodov.grsso.oauth_client.exception.OAuthInvalidScopeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OAuthScopeSetConverterTest {
    private OAuthScopeSetConverter converter;

    @BeforeEach
    void setUp() {
        this.converter = new OAuthScopeSetConverter();
    }

    @Test
    void convertToDatabaseColumn() {
        var attribute = Set.of(OAuthScope.PROFILE, OAuthScope.OPEN_ID);

        var result = converter.convertToDatabaseColumn(attribute);

        assertThat(result).contains("profile").contains("openid");
    }

    @Test
    void convertToEntityAttribute() {
        var dbData = "profile,openid";

        var result = converter.convertToEntityAttribute(dbData);

        assertThat(result).isEqualTo(Set.of(OAuthScope.PROFILE, OAuthScope.OPEN_ID));
    }

    @Test
    void convertToEntityAttribute_withInvalidDatabaseData_throwsIllegalArgumentException() {
        var dbData = "profile,open_id";

        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
            .isInstanceOf(OAuthInvalidScopeException.class);
    }
}