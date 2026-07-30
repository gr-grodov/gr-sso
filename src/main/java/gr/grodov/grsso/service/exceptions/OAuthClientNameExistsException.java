package gr.grodov.grsso.service.exceptions;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;

import java.util.List;

public class OAuthClientNameExistsException extends BaseErrorFieldException {

    public OAuthClientNameExistsException(String code, List<ErrorFieldDto> errorsField) {
        super(code, errorsField);
    }

    public OAuthClientNameExistsException(String code, ErrorFieldDto errorField) {
        super(code, errorField);
    }

    public OAuthClientNameExistsException(String code, String errorField, String errorFieldCode) {
        super(code, errorField, errorFieldCode);
    }
}
