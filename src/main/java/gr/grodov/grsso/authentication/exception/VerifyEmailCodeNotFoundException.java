package gr.grodov.grsso.authentication.exception;

import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class VerifyEmailCodeNotFoundException extends BaseErrorFieldException {
    public VerifyEmailCodeNotFoundException() {
        super("verify_email_code_not_found");
    }
}
