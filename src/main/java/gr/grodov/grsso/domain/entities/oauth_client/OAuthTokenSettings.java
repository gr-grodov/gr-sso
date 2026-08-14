package gr.grodov.grsso.domain.entities.oauth_client;

import lombok.*;

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
    private long refreshTokenTimeToLive = 2592000;

    @Builder.Default
    private boolean reuseRefreshTokens = true;
}