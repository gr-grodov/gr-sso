package gr.grodov.grsso.service.exceptions;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

public class EmailAlreadyExistsException extends BaseErrorFieldException {
    public EmailAlreadyExistsException() {
        super("email_invalid", "email", "already_exist");
    }
}
