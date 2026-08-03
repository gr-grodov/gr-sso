package gr.grodov.grsso.service.exceptions;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;

import java.util.List;

public class OAuthClientNameExistsException extends BaseErrorFieldException {

    public OAuthClientNameExistsException() {
        super("oauth_client_name_inlavid", "oauth_client.clientName", "exists");
    }
}
