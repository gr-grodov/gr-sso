package gr.grodov.grsso.api.advice;

import gr.grodov.grsso.api.dto.response.ErrorFieldDto;
import gr.grodov.grsso.api.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

//@RestControllerAdvice
public class ValidationExceptionAdvice {

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException ex) {
        System.out.println(ex);
        List<ErrorFieldDto> errors = ex.getFieldErrors().stream()
            .map(err -> new ErrorFieldDto(
                err.getField(),
                err.getCode(),
                err.getDefaultMessage()
            )).toList();

        return ErrorResponse.of("validation_error", errors);
    }*/
}