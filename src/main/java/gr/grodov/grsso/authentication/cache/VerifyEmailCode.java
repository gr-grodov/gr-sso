package gr.grodov.grsso.authentication.cache;

import gr.grodov.grsso.common.cache.CacheEntry;

import java.util.UUID;

@CacheEntry(keyPrefix = "verify_email_code")
public record VerifyEmailCode(
    UUID userId,
    String verifyCode,
    int remainAttempt
) {
}
