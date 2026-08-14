package gr.grodov.grsso.service.exceptions;

public class OAuth2ConsentException extends BaseErrorFieldException {
    public OAuth2ConsentException(String code) {
        super(code);
    }
}
