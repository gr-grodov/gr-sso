package gr.grodov.grsso.domain.converter;

import gr.grodov.grsso.domain.entities.oauth.OAuthClientAuthenticationMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class ClientAuthenticationMethodSetConverter implements AttributeConverter<Set<OAuthClientAuthenticationMethod>, String> {
    @Override
    public String convertToDatabaseColumn(Set<OAuthClientAuthenticationMethod> attribute) {
        return attribute.stream().map(OAuthClientAuthenticationMethod::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<OAuthClientAuthenticationMethod> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(","))
            .map(OAuthClientAuthenticationMethod::valueOf)
            .collect(Collectors.toSet());
    }
}
