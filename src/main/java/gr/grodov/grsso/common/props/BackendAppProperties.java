package gr.grodov.grsso.common.props;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Valid
@ConfigurationProperties(prefix = "grsso.app")
public record BackendAppProperties(
    @NotBlank String backendUri
) {
}
