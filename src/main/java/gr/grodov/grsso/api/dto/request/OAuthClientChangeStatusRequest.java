package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClientChangeStatusRequest {

    @NotBlank
    private String id;

    @NotNull
    private OAuthClientStatus status;
}