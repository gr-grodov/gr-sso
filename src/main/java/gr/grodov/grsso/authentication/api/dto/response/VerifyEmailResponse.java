package gr.grodov.grsso.authentication.api.dto.response;

public record VerifyEmailResponse(
    Boolean success,
    Integer remainAttempt
) {
}
