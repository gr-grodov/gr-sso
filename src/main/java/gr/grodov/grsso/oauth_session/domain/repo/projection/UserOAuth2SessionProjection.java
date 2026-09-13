package gr.grodov.grsso.oauth_session.domain.repo.projection;

import java.time.Instant;
import java.util.UUID;

public interface UserOAuth2SessionProjection {
    UUID getUserId();
    UUID getSid();
    String getClientId();
    String getClientName();
    Instant getLastUsedAt();
}
