package gr.grodov.grsso.oauth_client.domain.entity;

import lombok.*;
import org.springframework.modulith.NamedInterface;

@NamedInterface("domain")
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