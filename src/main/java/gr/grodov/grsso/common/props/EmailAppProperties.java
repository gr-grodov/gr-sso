package gr.grodov.grsso.common.props;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "grsso.email")
public record EmailAppProperties(
    @NotBlank String fromAddress,
    @NotBlank String fromName,
    @NotNull @Valid VerifyEmailCode verifyEmailCode
) {
    public record VerifyEmailCode(
        @NotNull Integer minuteTime,
        @NotNull Integer attempt
    ) {}
}
