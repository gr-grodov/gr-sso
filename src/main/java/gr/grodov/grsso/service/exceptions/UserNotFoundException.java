package gr.grodov.grsso.service.exceptions;

public class UserNotFoundException extends BaseErrorFieldException {
    public UserNotFoundException() {
        super("user_not_found");
    }
}
