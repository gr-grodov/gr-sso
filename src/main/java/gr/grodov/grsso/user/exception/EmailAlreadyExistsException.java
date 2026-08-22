package gr.grodov.grsso.user.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class EmailAlreadyExistsException extends BaseErrorFieldException {
    public EmailAlreadyExistsException() {
        super("email_invalid", "email", "already_exist");
    }
}
