package gr.grodov.grsso.user.api.dto.request;

import jakarta.validation.constraints.Pattern;
import org.jspecify.annotations.Nullable;

public record UserProfileInfoRequest(
    @Nullable
    @Pattern(regexp = "^\\p{L}[\\p{L}\\s]*\\p{L}$", message = "invalid")
    String firstName,
    @Nullable
    @Pattern(regexp = "^\\p{L}[\\p{L}\\s]*\\p{L}$", message = "invalid")
    String lastName,
    @Nullable
    @Pattern(regexp = "^\\p{L}[\\p{L}\\s]*\\p{L}$", message = "invalid")
    String patronymic
) {
}
