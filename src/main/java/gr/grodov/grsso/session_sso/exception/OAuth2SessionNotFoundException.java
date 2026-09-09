package gr.grodov.grsso.session_sso.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuth2SessionNotFoundException extends BaseErrorFieldException {
    public OAuth2SessionNotFoundException() {
        super("oauth2_session_not_found");
    }

}
