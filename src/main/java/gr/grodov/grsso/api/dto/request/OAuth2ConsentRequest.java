package gr.grodov.grsso.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2ConsentRequest {
    @NotBlank
    private String clientId;
    @NotBlank
    private String state;
    @Min(1)
    private Set<String> scopes;
}
