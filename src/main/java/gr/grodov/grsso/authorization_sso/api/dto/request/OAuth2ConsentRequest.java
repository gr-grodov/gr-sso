package gr.grodov.grsso.authorization_sso.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2ConsentRequest {
    @NotBlank(message = "empty")
    private String clientId;

    @NotBlank(message = "empty")
    private String state;

    @Size(min = 1, message = "min")
    private Set<String> scopes;
}
