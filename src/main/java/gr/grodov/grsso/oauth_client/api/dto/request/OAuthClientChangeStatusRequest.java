package gr.grodov.grsso.oauth_client.api.dto.request;

import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClientChangeStatusRequest {

    @NotBlank(message = "empty")
    private String id;

    @NotNull(message = "empty")
    private OAuthClientStatus status;
}