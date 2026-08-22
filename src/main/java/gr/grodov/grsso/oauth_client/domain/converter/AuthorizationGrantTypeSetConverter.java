package gr.grodov.grsso.oauth_client.domain.converter;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class AuthorizationGrantTypeSetConverter implements AttributeConverter<Set<OAuthAuthorizationGrantType>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<OAuthAuthorizationGrantType> attribute) {
        return attribute.stream().map(OAuthAuthorizationGrantType::name).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<OAuthAuthorizationGrantType> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(DELIMITER))
            .map(OAuthAuthorizationGrantType::valueOf)
            .collect(Collectors.toSet());
    }
}
