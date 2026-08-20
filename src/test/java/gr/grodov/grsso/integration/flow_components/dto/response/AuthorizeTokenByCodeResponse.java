package gr.grodov.grsso.integration.flow_components.dto.response;

public record AuthorizeTokenByCodeResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    String idToken,
    String scope
) {
}
