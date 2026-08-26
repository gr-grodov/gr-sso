package gr.grodov.grsso.authentication.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshVerifyCodeRequest {

    @NotBlank(message = "empty")
    private String verifyId;
}
