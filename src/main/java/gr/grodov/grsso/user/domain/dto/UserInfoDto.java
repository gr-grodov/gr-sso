package gr.grodov.grsso.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

@NamedInterface("domain")
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

