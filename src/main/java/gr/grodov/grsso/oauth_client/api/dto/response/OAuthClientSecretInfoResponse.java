package gr.grodov.grsso.oauth_client.api.dto.response;

public record OAuthClientSecretInfoResponse(
    String clientID,
    String clientSecret
) {
}
