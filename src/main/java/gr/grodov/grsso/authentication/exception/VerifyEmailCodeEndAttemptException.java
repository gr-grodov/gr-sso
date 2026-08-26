package gr.grodov.grsso.authentication.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class VerifyEmailCodeEndAttemptException extends BaseErrorFieldException {
    public VerifyEmailCodeEndAttemptException() {
        super("verify_email_code_end_attempt");
    }
}
