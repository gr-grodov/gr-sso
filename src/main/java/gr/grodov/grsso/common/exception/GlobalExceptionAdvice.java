package gr.grodov.grsso.common.exception;

import gr.grodov.grsso.common.api.ErrorFieldDto;
import gr.grodov.grsso.common.api.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException ex) {
        List<ErrorFieldDto> errors = ex.getFieldErrors().stream()
            .map(err -> new ErrorFieldDto(
                err.getField(),
                err.getDefaultMessage()
            )).toList();

        return ErrorResponse.of("validation_error", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Exception ex) {
        return ErrorResponse.of("unknown", ex.getMessage());
    }

    @ExceptionHandler(BaseErrorFieldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BaseErrorFieldException ex) {
        return ErrorResponse.of(ex.getCode(), ex.getErrorsField());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(BadCredentialsException ex) {
        return ErrorResponse.of("auth_bad_credentials");
    }
}
