package gr.grodov.grsso.authentication.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailRequest {
    @NotBlank(message = "empty")
    private String verifyId;

    @NotBlank(message = "empty")
    @Pattern(regexp = "^[0-9]{6}$", message = "invalid")
    private String verifyCode;
}
