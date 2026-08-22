package gr.grodov.grsso.common.exception;

import gr.grodov.grsso.common.api.ErrorFieldDto;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class BaseErrorFieldException extends RuntimeException {
    private final String code;
    private final List<ErrorFieldDto> errorsField;

    public BaseErrorFieldException(String code) {
        this.code = code;
        this.errorsField = List.of();
    }

    public BaseErrorFieldException(String code, List<ErrorFieldDto> errorsField) {
        this.code = code;
        this.errorsField = errorsField;
    }

    public BaseErrorFieldException(String code, ErrorFieldDto errorField) {
        this.code = code;
        this.errorsField = List.of(errorField);
    }

    public BaseErrorFieldException(String code, String errorField, String errorFieldCode) {
        this.code = code;
        this.errorsField = List.of(new ErrorFieldDto(errorField, errorFieldCode));
    }
}
