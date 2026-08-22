package gr.grodov.grsso.oauth_client.domain.converter;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class OAuthScopeSetConverter implements AttributeConverter<Set<OAuthScope>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<OAuthScope> attribute) {
        return attribute.stream().map(OAuthScope::getScopeValue).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<OAuthScope> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(DELIMITER))
            .map(OAuthScope::scopeValueOf)
            .collect(Collectors.toSet());
    }
}
