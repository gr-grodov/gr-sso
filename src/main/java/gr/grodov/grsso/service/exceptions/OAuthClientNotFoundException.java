package gr.grodov.grsso.service.exceptions;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;

import java.util.List;

public class OAuthClientNotFoundException extends BaseErrorFieldException {
    public OAuthClientNotFoundException() {
        super("oauth_client_not_found");
    }
}
