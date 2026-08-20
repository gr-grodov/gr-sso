package gr.grodov.grsso.integration.flow_components.dto.request;

public record OAuthClientRefreshTokenRequest(
    String clientId,
    String clientSecret,
    String refreshToken
) {
}
