package gr.grodov.grsso.common.event;

import java.util.List;
import java.util.UUID;

public record OAuthLogoutEvent(
    List<UUID> sids,
    UUID userId
) {
}
