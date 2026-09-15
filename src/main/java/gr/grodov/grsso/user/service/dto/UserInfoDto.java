package gr.grodov.grsso.user.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.modulith.NamedInterface;

import java.util.UUID;

@NamedInterface("service")
@Builder
public record UserInfoDto(
    @NotNull
    UUID id,

    @NotNull
    @Email
    String email,

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password,

    Boolean enabled,

    String externalId,

    AuthProvider provider,

    Role role,

    String firstName,

    String lastName,

    String patronymic,

    UUID avatarId
) {
}

