package gr.grodov.grsso.oauth_client.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class OAuthClientNotFoundException extends BaseErrorFieldException {
    public OAuthClientNotFoundException() {
        super("oauth_client_not_found");
    }
}
