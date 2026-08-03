package gr.grodov.grsso.api.dto.request;

import gr.grodov.grsso.domain.entities.oauth.OAuthClientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthClientChangeStatusRequest {
    private String id;
    private OAuthClientStatus status;
}