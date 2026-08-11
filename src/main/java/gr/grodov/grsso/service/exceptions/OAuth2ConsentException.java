package gr.grodov.grsso.service.exceptions;

public class OAuth2ConsentException extends BaseErrorFieldException {
    public OAuth2ConsentException() {
        super("consent_error");
    }
}
