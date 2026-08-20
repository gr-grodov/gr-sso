package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.api.validator.annotation.AllowAuthGrantTypes;
import gr.grodov.grsso.api.validator.annotation.AllowAuthMethods;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClient;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthScope;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClientRequest {

    private String id;

    @NotBlank
    private String clientName;

    @Size(min = 1, message = "min")
    private Set<String> redirectUris;

    @Size(min = 1, message = "min")
    private Set<OAuthScope> scopes;

    @AllowAuthGrantTypes
    private Set<OAuthAuthorizationGrantType> authorizationGrantTypes;

    @AllowAuthMethods
    private Set<OAuthClientAuthenticationMethod> clientAuthenticationMethods;
}
