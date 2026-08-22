package gr.grodov.grsso.authorization_sso.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuthPrincipalNotFoundException extends BaseErrorFieldException {
    public OAuthPrincipalNotFoundException() {
        super("user_error_loading");
    }
}
