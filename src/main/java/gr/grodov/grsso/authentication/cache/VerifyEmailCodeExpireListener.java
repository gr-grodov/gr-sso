package gr.grodov.grsso.authentication.cache;

import gr.grodov.grsso.authentication.service.VerifyEmailService;
import gr.grodov.grsso.common.cache.CacheKeyExpireListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerifyEmailCodeExpireListener implements CacheKeyExpireListener<VerifyEmailCode> {

    private final VerifyEmailService verifyEmailService;

    @Override
    public void onEvent(String key) {
        verifyEmailService.handleExpireId(key);
    }
}
