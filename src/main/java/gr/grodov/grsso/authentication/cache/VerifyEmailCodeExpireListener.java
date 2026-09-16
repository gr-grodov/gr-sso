package gr.grodov.grsso.authentication.cache;

import gr.grodov.grsso.authentication.service.VerifyEmailService;
import gr.grodov.grsso.common.cache.CacheKeyExpireListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyEmailCodeExpireListener implements CacheKeyExpireListener<VerifyEmailCode> {

    private final VerifyEmailService verifyEmailService;

    @Override
    public void onEvent(String key) {
        log.debug("Handle expire verifyEmailCode {}", key);
        verifyEmailService.handleExpireId(key);
    }
}
