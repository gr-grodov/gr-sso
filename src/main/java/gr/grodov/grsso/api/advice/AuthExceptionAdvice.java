package gr.grodov.grsso.api.advice;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import gr.grodov.grsso.api.dto.response.ErrorResponse;
import gr.grodov.grsso.service.exceptions.BaseErrorFieldException;
import gr.grodov.grsso.service.exceptions.EmailAlreadyExistsException;
import gr.grodov.grsso.service.exceptions.OAuthAuthorizationGoneException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionAdvice {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(BaseErrorFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BaseErrorFieldException ex) {
        return ErrorResponse.of(ex.getCode(), ex.getErrorsField());
    }

    @ExceptionHandler(OAuthAuthorizationGoneException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handle(OAuthAuthorizationGoneException ex) {
        return ErrorResponse.of(ex.getCode(), ex.getErrorsField());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BadCredentialsException ex) {
        return ErrorResponse.of("auth_bad_credentials");
    }

}
