package gr.grodov.grsso.oauth_client.domain.repo;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthClient;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NamedInterface("repo")
@Repository
public interface OAuthClientRepo extends JpaRepository<OAuthClient, String> {
    List<OAuthClient> findAllByOrderByUpdatedAtDesc();
    Optional<OAuthClient> findByIdAndStatus(String id, OAuthClientStatus status);
    Optional<OAuthClient> findByClientId(String clientId);
    Optional<OAuthClient> findByClientIdAndStatus(String clientId, OAuthClientStatus status);
    Boolean existsByClientName(String clientName);
}
