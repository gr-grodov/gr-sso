package gr.grodov.grsso.api.dto.response;

public record OAuthClientSecretInfoResponse(
    String clientID,
    String clientSecret
) {
}
