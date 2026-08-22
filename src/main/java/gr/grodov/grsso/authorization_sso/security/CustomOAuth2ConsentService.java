package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsent;
import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsentId;
import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.authorization_sso.domain.repo.OAuthConsentRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.modulith.NamedInterface;
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
