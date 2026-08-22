package gr.grodov.grsso.user.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class UserNotFoundException extends BaseErrorFieldException {
    public UserNotFoundException() {
        super("user_not_found");
    }
}
