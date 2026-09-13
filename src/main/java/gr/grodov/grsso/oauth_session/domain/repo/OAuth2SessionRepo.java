package gr.grodov.grsso.oauth_session.domain.repo;

import gr.grodov.grsso.oauth_session.domain.entity.OAuth2Session;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserIdentificationProjection;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserOAuth2SessionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuth2SessionRepo extends JpaRepository<OAuth2Session, UUID> {
    Optional<OAuth2Session> findByAuthorizationId(String authorizationId);
    Optional<OAuth2Session> findByUserIdAndClientIdAndDeviceId(UUID userId, String clientId, String deviceId);
    Optional<OAuth2Session> findBySidAndUserId(UUID sid, UUID userId);
    @Modifying
    @Query("UPDATE OAuth2Session s SET s.lastUsedAt = :lastUsedAt, s.authorizationId = :authorizationId WHERE s.sid = :sid")
    void updateAuthorization(UUID sid, Instant lastUsedAt, String authorizationId);
    List<OAuth2Session> findAllByUserId(UUID userId);
    boolean existsBySidAndUserId(UUID sid, UUID userId);

    @Query(
        value = """
            SELECT DISTINCT user_id AS userId, user_email AS userEmail
            FROM v_user_client_sessions
            WHERE client_name ILIKE CONCAT('%', :searchPhrase, '%') OR user_email ILIKE CONCAT('%', :searchPhrase, '%')
            ORDER BY user_email
            """,
        countQuery = """
            SELECT COUNT(DISTINCT user_id)
            FROM v_user_client_sessions
            WHERE client_name ILIKE CONCAT('%', :searchPhrase, '%') OR user_email ILIKE CONCAT('%', :searchPhrase, '%')
            """,
        nativeQuery = true
    )
    Page<UserIdentificationProjection> searchUsers(@Param("searchPhrase") String search, Pageable pageable);

    @Query(
        value = """
            SELECT DISTINCT user_id AS userId, user_email AS userEmail
            FROM v_user_client_sessions
            ORDER BY user_email
            """,
        countQuery = """
            SELECT COUNT(DISTINCT user_id)
            FROM v_user_client_sessions
            """,
        nativeQuery = true
    )
    Page<UserIdentificationProjection> findAllUsers(Pageable pageable);

    @Query(
        value = """
            SELECT user_id AS userId, sid AS sid, client_id AS clientId,
                   client_name AS clientName, last_used_at AS lastUsedAt
            FROM v_user_client_sessions
            WHERE user_id IN (:userIds)
            ORDER BY user_id, last_used_at DESC
            """,
        nativeQuery = true
    )
    List<UserOAuth2SessionProjection> findSessionsForUsers(@Param("userIds") List<UUID> userIds);

    @Query(
        value = """
            SELECT user_id AS userId, sid AS sid, client_id AS clientId,
                   client_name AS clientName, last_used_at AS lastUsedAt
            FROM v_user_client_sessions
            WHERE user_id = :userId AND client_id = :clientId
            """,
        nativeQuery = true
    )
    List<UserOAuth2SessionProjection> findSessionsForUserClient(@Param("userId") UUID userId, @Param("clientId") String clientId);
}
