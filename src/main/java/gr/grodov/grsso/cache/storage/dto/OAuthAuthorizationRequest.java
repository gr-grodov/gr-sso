package gr.grodov.grsso.cache.storage.dto;

import java.time.Instant;

public record OAuthAuthorizationRequest(
    String requestUri,
    Instant createdAt
) {}
