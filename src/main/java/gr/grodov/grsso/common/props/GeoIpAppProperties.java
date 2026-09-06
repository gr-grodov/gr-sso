package gr.grodov.grsso.common.props;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Validated
@ConfigurationProperties(prefix = "grsso.geoip")
public record GeoIpAppProperties(
    @NotNull
    Path databasePath,
    @NotBlank
    @DefaultValue("0 0 6 2 * *")
    String reloadCron
) {
}
