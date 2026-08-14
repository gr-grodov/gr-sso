package gr.grodov.grsso.domain.mapper;

import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsent;
import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsentId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OAuth2AuthorizationConsentMapper implements Mapper<OAuthConsent, OAuth2AuthorizationConsent> {
    @Override
    public OAuth2AuthorizationConsent fromDB(OAuthConsent consent) {
         OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
             consent.getId().getRegisteredClientId(),
             consent.getId().getPrincipalName()
         );

         consent.getAuthorities().forEach(auth ->
             builder.authority(new SimpleGrantedAuthority(auth))
         );

         return builder.build();
    }

    @Override
    public OAuthConsent toDB(OAuth2AuthorizationConsent consent) {
        return OAuthConsent.builder()
            .id(new OAuthConsentId(consent.getRegisteredClientId(), consent.getPrincipalName()))
            .authorities(consent.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
            )
        .build();
    }
}
