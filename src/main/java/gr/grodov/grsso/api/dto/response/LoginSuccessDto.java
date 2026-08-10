package gr.grodov.grsso.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginSuccessDto {
    private Boolean oauthLogin;
    private String redirectURI;
}
