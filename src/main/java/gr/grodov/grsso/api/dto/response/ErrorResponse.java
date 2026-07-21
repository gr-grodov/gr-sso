package gr.grodov.grsso.api.dto.response;

import java.util.List;

public record ErrorResponse(
    String code,
    String message,
    List<ErrorFieldDto> errors
) {
    public static ErrorResponse of(String code, List<ErrorFieldDto> errors) {
        return new ErrorResponse(code, null, errors);
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, List.of());
    }

    public static ErrorResponse of(String code) {
        return new ErrorResponse(code, null, List.of());
    }
}


