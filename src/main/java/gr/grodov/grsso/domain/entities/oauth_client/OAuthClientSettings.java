package gr.grodov.grsso.domain.entities.oauth_client;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClientSettings {

    @Builder.Default
    private boolean requireAuthorizationConsent = true;

    @Builder.Default
    private boolean requireProofKey = false;

    private String jwkSetUrl;

    @Builder.Default
    private String tokenEndpointAuthenticationSigningAlgorithm = "RS256";

    @Builder.Default
    private String oidcLogoutRedirectUri = null;
}