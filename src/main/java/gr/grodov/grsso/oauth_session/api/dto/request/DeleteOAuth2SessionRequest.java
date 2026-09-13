package gr.grodov.grsso.oauth_session.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteOAuth2SessionRequest(
    @NotBlank(message = "empty")
    String clientId,
    @NotNull(message = "empty")
    UUID userId
) {
}
