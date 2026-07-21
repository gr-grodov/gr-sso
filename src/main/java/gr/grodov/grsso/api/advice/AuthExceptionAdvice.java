package gr.grodov.grsso.api.advice;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import gr.grodov.grsso.api.dto.response.ErrorResponse;
import gr.grodov.grsso.security.exceptions.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionAdvice {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(EmailAlreadyExistsException ex) {
        ErrorFieldDto errorField = new ErrorFieldDto("email", "already_exist");
        return ErrorResponse.of("email_invalid", List.of(errorField));
    }
}
