package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.domain.entities.oauth.OAuthAuthorizationGrantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import tools.jackson.databind.annotation.EnumNaming;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClientRequest {
    private String clientName;
    private Set<String> redirectUris;
    private Set<String> scopes;
    private Set<OAuthAuthorizationGrantType> authorizationGrantTypes;
}
