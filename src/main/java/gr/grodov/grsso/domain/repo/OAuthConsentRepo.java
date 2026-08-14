package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsent;
import gr.grodov.grsso.domain.entities.oauth_consent.OAuthConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthConsentRepo extends JpaRepository<OAuthConsent, OAuthConsentId> {
}
