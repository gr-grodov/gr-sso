package gr.grodov.grsso.security.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {
    public EmailAlreadyExistsException(@Nullable String msg, Throwable cause) {
        super(msg, cause);
    }

    public EmailAlreadyExistsException(@Nullable String msg) {
        super(msg);
    }

    public EmailAlreadyExistsException() {
        super(null);
    }
}
