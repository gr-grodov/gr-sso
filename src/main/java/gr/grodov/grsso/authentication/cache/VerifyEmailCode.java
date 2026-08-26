package gr.grodov.grsso.authentication.cache;

import gr.grodov.grsso.common.cache.CacheEntry;

@CacheEntry(keyPrefix = "verify_email_code")
public record VerifyEmailCode(
    Long userId,
    String verifyCode,
    int remainAttempt
) {
}
