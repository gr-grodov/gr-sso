package gr.grodov.grsso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserInfoDto(
    @NotNull
    Long id,

    @NotNull
    @Email
    String email,

    String password,

    Boolean enabled
) {
}
