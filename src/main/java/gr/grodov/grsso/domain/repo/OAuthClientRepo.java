package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthClient;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthClientRepo extends JpaRepository<OAuthClient, String> {
    List<OAuthClient> findAllByOrderByUpdatedAtDesc();
    Optional<OAuthClient> findByIdAndStatus(String id, OAuthClientStatus status);
    Optional<OAuthClient> findByClientId(String clientId);
    Optional<OAuthClient> findByClientIdAndStatus(String clientId, OAuthClientStatus status);
    Boolean existsByClientName(String clientName);
}
