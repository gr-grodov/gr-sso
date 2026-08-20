package gr.grodov.grsso.integration.helpers.dto;

public record ConsentPageParams(
    String state,
    String clientId,
    String scope
) {
}
