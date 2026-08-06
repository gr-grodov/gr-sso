package gr.grodov.grsso.service.exceptions;

public class OAuthAuthorizationGoneException extends BaseErrorFieldException {
    public OAuthAuthorizationGoneException() {
        super("oauth_request_gone");
    }
}
