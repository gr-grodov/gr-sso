package gr.grodov.grsso.authentication.exception;

import gr.grodov.grsso.common.api.ErrorFieldDto;
import gr.grodov.grsso.common.exception.BaseErrorFieldException;

public class VerifyEmailCodeInvalidCodeException extends BaseErrorFieldException {
    public VerifyEmailCodeInvalidCodeException() {
        super("verify_email_code_invalid_code", new ErrorFieldDto("verifyCode", "invalid"));
    }
}
