package gr.grodov.grsso.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RegistrationRequest(
    @NotNull
    @Email
    String email,

    @NotNull
    @Min(8)
    String password
) {
}
