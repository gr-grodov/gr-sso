package gr.grodov.grsso.authentication.api.dto.response;

public record RegistrationResponse(
    String userEmail,
    String verifyId
) {
}
