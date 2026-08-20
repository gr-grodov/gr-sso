package gr.grodov.grsso.integration.helpers.dto;

public record AuthorizeParams(
    String code,
    String state
) {
}
