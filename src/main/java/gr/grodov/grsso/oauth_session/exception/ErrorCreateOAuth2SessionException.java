package gr.grodov.grsso.oauth_session.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class ErrorCreateOAuth2SessionException extends BaseErrorFieldException {
    public ErrorCreateOAuth2SessionException() {
        super("error_create_oauth2_session");
    }
}
