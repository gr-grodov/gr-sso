package gr.grodov.grsso.authorization_sso.domain.repo;

import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsent;
import gr.grodov.grsso.authorization_sso.domain.entity.OAuthConsentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthConsentRepo extends JpaRepository<OAuthConsent, OAuthConsentId> {
}
