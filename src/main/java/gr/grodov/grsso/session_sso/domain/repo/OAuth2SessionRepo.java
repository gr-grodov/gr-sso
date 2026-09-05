package gr.grodov.grsso.session_sso.domain.repo;

import gr.grodov.grsso.session_sso.domain.entity.OAuth2Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuth2SessionRepo extends JpaRepository<OAuth2Session, UUID> {
    Optional<OAuth2Session> findByAuthorizationId(String authorizationId);
    Optional<OAuth2Session> findByUserIdAndClientIdAndDeviceId(String userId, String clientId, String deviceId);
    @Modifying
    @Query("UPDATE OAuth2Session s SET s.lastUsedAt = CURRENT_TIMESTAMP, s.authorizationId = :authorizationId WHERE s.sid = :sid")
    void updateAuthorization(UUID sid, String authorizationId);
}
