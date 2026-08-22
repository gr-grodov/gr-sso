package gr.grodov.grsso.oauth_client.props;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

@Validated
@ConfigurationProperties(prefix = "grsso.oauth")
public record OAuthAppProperties(
    @NotEmpty Set<String> authenticationGrantTypes,
    @NotEmpty Set<String> authenticationMethods
) {
}
