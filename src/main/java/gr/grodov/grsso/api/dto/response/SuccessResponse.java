package gr.grodov.grsso.api.dto.response;

public record SuccessResponse<T>(
    boolean success,
    String message,
    T data
) {
    public static SuccessResponse<Void> of(Boolean success) {
        return new SuccessResponse<>(success, null, null);
    }

    public static SuccessResponse<Void> of(String message) {
        return new SuccessResponse<>(true, message, null);
    }

    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(true, message, data);
    }
}