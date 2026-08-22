package gr.grodov.grsso.oauth_client.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuthClientInvalidException extends BaseErrorFieldException {
    public OAuthClientInvalidException(String errorField, String errorFieldCode) {
        super("oauth_client_inlavid", errorField, errorFieldCode);
    }
}
