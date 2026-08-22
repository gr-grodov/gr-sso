package gr.grodov.grsso.oauth_client.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuthInvalidScopeException extends BaseErrorFieldException {
    public OAuthInvalidScopeException() {
        super("invalid_oauth_scope");
    }
}
