package gr.grodov.grsso.domain.repo;

import gr.grodov.grsso.domain.entities.oauth.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthClientRepo extends JpaRepository<OAuthClient, String> {
    List<OAuthClient> findAllByOrderByUpdatedAtDesc();
    Optional<OAuthClient> findByClientId(String clientId);
    Boolean existsByClientName(String clientName);
}
