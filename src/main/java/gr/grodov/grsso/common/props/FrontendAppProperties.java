package gr.grodov.grsso.common.props;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

@Validated
@ConfigurationProperties(prefix = "grsso.frontend")
public record FrontendAppProperties(
    @NotBlank String url,
    @NotNull @Valid Endpoints endpoints
) {

    public record Endpoints(
        @NotBlank String providerError,
        @NotBlank String login,
        @NotBlank String oauthConsent
    ) { }

    public String loginUrl() {
        return buildUrl(endpoints.login());
    }

    public String providerErrorUrl() {
        return buildUrl(endpoints.providerError());
    }

    public String oauthConsentUrl() {
        return buildUrl(endpoints.oauthConsent());
    }

    private String buildUrl(String path) {
        return UriComponentsBuilder.fromUriString(url)
            .path(path)
            .toUriString();
    }
}