package gr.grodov.grsso.common.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "grsso.verify-email")
public record VerifyEmailAppProperties(
    Integer minuteTime,
    Integer attempt,
    String fromAddress,
    String fromName
) {
}
