package gr.grodov.grsso.service.exceptions;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

public class EmailAlreadyExistsException extends BaseErrorFieldException {

    public EmailAlreadyExistsException(String code, List<ErrorFieldDto> errorsField) {
        super(code, errorsField);
    }

    public EmailAlreadyExistsException(String code, ErrorFieldDto errorField) {
        super(code, errorField);
    }

    public EmailAlreadyExistsException(String code, String errorField, String errorFieldCode) {
        super(code, errorField, errorFieldCode);
    }
}
