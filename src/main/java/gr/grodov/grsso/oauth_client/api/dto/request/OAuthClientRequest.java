package gr.grodov.grsso.oauth_client.api.dto.request;

import gr.grodov.grsso.oauth_client.api.dto.validator.annotation.AllowAuthGrantTypes;
import gr.grodov.grsso.oauth_client.api.dto.validator.annotation.AllowAuthMethods;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
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
