package gr.grodov.grsso.domain.entities.oauth;

import gr.grodov.grsso.domain.converter.DurationToSecondsSerializer;
import gr.grodov.grsso.domain.converter.SecondsToDurationDeserializer;
import lombok.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.time.Duration;

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