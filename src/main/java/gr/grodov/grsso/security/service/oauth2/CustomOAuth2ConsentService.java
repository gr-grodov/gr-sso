package gr.grodov.grsso.security.service.oauth2;

import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsent;
import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsentId;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.OAuthConsentRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2ConsentService implements OAuth2AuthorizationConsentService {

    private final OAuthConsentRepo oAuthConsentRepo;
    private final Mapper<OAuthConsent, OAuth2AuthorizationConsent> oAuthConsentMapper;

    @Override
    public void save(@NonNull OAuth2AuthorizationConsent authorizationConsent) {
        oAuthConsentRepo.save(oAuthConsentMapper.toDB(authorizationConsent));
    }

    @Override
    public void remove(@NonNull OAuth2AuthorizationConsent authorizationConsent) {
        oAuthConsentRepo.delete(oAuthConsentMapper.toDB(authorizationConsent));
    }

    @Override
    public @Nullable OAuth2AuthorizationConsent findById(
        @NonNull String registeredClientId,
        @NonNull String principalName
    ) {
        OAuthConsent oAuthConsent = oAuthConsentRepo
            .findById(new OAuthConsentId(registeredClientId, principalName))
            .orElse(null);

        if (oAuthConsent != null) {
            return oAuthConsentMapper.fromDB(oAuthConsent);
        }
        return null;
    }
}
