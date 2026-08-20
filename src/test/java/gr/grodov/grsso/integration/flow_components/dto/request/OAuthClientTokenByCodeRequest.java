package gr.grodov.grsso.integration.flow_components.dto.request;

public record OAuthClientTokenByCodeRequest(
    String clientId,
    String clientSecret,
    String authorizationCode,
    String redirectURI
) {
}
