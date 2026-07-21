package gr.grodov.grsso.api.dto.response;

public record ErrorFieldDto(
    String field,
    String code
) {
}
