package gr.grodov.grsso.service.exceptions;

public class OAuthInvalidScopeException extends BaseErrorFieldException {
    public OAuthInvalidScopeException() {
        super("invalid_oauth_scope");
    }
}
