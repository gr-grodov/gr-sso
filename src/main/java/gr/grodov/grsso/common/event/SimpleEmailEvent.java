package gr.grodov.grsso.common.event;

public record SimpleEmailEvent(
    String toAddress,
    String subject,
    String message
) {
}
