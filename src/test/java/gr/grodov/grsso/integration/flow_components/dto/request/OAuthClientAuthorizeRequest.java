package gr.grodov.grsso.integration.flow_components.dto.request;

import java.util.List;

public record OAuthClientAuthorizeRequest(
    String responseType,
    String clientID,
    String redirectURI,
    List<String> scope,
    String state
) {
}
