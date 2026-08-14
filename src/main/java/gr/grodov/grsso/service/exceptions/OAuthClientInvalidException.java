package gr.grodov.grsso.service.exceptions;

public class OAuthClientInvalidException extends BaseErrorFieldException {
    public OAuthClientInvalidException(String errorField, String errorFieldCode) {
        super("oauth_client_inlavid", errorField, errorFieldCode);
    }
}
