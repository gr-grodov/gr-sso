package gr.grodov.grsso.oauth_session.domain.repo;

import gr.grodov.grsso.oauth_session.domain.entity.OAuth2Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuth2SessionRepo extends JpaRepository<OAuth2Session, UUID> {
    Optional<OAuth2Session> findByAuthorizationId(String authorizationId);
    Optional<OAuth2Session> findByUserIdAndClientIdAndDeviceId(UUID userId, String clientId, String deviceId);
    Optional<OAuth2Session> findBySidAndUserId(UUID sid, String userId);
    @Modifying
    @Query("UPDATE OAuth2Session s SET s.lastUsedAt = :lastUsedAt, s.authorizationId = :authorizationId WHERE s.sid = :sid")
    void updateAuthorization(UUID sid, Instant lastUsedAt, String authorizationId);
    List<OAuth2Session> findAllByUserId(UUID userId);
}
