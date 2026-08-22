package gr.grodov.grsso.authorization_sso.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuth2ConsentException extends BaseErrorFieldException {
    public OAuth2ConsentException(String code) {
        super(code);
    }
}
