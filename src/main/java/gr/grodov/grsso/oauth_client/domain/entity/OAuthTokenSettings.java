package gr.grodov.grsso.oauth_client.domain.entity;

import lombok.*;
import org.springframework.modulith.NamedInterface;

@NamedInterface("domain")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthTokenSettings {

    @Builder.Default
    private long authorizationCodeTimeToLive = 300;

    @Builder.Default
    private long accessTokenTimeToLive = 300;

    @Builder.Default
    private long refreshTokenTimeToLive = 2_592_000L;

    @Builder.Default
    private boolean reuseRefreshTokens = false;
}