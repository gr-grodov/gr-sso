package gr.grodov.grsso.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.domain.entities.Role;
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

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password,

    Boolean enabled,

    String externalId,

    AuthProvider provider,

    Role role
) {
}
