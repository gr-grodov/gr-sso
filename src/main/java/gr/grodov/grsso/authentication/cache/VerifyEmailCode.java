package gr.grodov.grsso.authentication.cache;

import gr.grodov.grsso.common.cache.CacheEntry;

@CacheEntry
public record VerifyEmailCode(
    Long userId,
    String verifyCode,
    int remainAttempt
) {
}
