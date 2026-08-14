package gr.grodov.grsso.service.exceptions;

public class OAuthPrincipalNotFoundException extends BaseErrorFieldException {
    public OAuthPrincipalNotFoundException() {
        super("user_error_loading");
    }
}
