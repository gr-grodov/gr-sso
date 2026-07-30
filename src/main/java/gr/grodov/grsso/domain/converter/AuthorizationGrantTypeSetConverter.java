package gr.grodov.grsso.domain.converter;

import gr.grodov.grsso.domain.entities.oauth.OAuthAuthorizationGrantType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class AuthorizationGrantTypeSetConverter implements AttributeConverter<Set<OAuthAuthorizationGrantType>, String> {
    @Override
    public String convertToDatabaseColumn(Set<OAuthAuthorizationGrantType> attribute) {
        return attribute.stream().map(OAuthAuthorizationGrantType::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<OAuthAuthorizationGrantType> convertToEntityAttribute(String dbData) {
        return Arrays.stream(dbData.split(","))
            .map(OAuthAuthorizationGrantType::valueOf)
            .collect(Collectors.toSet());
    }
}
