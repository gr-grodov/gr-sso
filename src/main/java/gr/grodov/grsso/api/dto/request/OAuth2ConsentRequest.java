package gr.grodov.grsso.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2ConsentRequest {
    private String clientId;
    private String state;
    private Set<String> scopes;
}
