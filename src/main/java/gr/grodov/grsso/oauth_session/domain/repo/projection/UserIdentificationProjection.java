package gr.grodov.grsso.oauth_session.domain.repo.projection;

import java.util.UUID;

public interface UserIdentificationProjection {
    UUID getUserId();
    String getUserEmail();
}
