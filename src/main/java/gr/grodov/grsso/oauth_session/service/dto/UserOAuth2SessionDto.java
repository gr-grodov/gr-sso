package gr.grodov.grsso.oauth_session.service.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserOAuth2SessionDto(
    UUID userId,
    String userEmail,
    List<OAuthClientShortInfo> clients
) {

    public record OAuthClientShortInfo(
        String clientId,
        String clientName,
        int countSessions
    ) { }
}
