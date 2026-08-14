package gr.grodov.grsso.domain.converter;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class ClientAuthenticationMethodSetConverter implements AttributeConverter<Set<OAuthClientAuthenticationMethod>, String> {

    private static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(Set<OAuthClientAuthenticationMethod> attribute) {
        return attribute.stream().map(OAuthClientAuthenticationMethod::name).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public Set<OAuthClientAuthenticationMethod> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(DELIMITER))
            .map(OAuthClientAuthenticationMethod::valueOf)
            .collect(Collectors.toSet());
    }
}
