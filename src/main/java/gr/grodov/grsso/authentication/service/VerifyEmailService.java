package gr.grodov.grsso.authentication.service;

import gr.grodov.grsso.authentication.api.dto.request.RefreshVerifyCodeRequest;
import gr.grodov.grsso.authentication.api.dto.request.VerifyEmailRequest;
import gr.grodov.grsso.authentication.cache.VerifyEmailCode;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeEndAttemptException;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeInvalidCodeException;
import gr.grodov.grsso.authentication.exception.VerifyEmailCodeNotFoundException;
import gr.grodov.grsso.common.cache.CacheStorage;
import gr.grodov.grsso.common.event.FromResourceEmailEvent;
import gr.grodov.grsso.common.props.EmailAppProperties;
import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyEmailService {

    private final ApplicationEventPublisher publisher;
    private final CacheStorage<VerifyEmailCode> verifyEmailCodeStorage;
    private final UserInfoService userInfoService;
    private final EmailAppProperties emailProperties;

    public String sendVerifyCode(UserInfoDto userInfo, Locale locale) {
        String verifyCode = getVerifyCode();
        String verifyId = generateVerifyId(userInfo.id());

        VerifyEmailCode verifyEmailCode = new VerifyEmailCode(userInfo.id(), verifyCode, emailProperties.verifyEmailCode().attempt());
        verifyEmailCodeStorage.save(verifyId, verifyEmailCode, Duration.ofMinutes(emailProperties.verifyEmailCode().minuteTime()));

        sendEmail(userInfo.email(), verifyCode, locale);

        return verifyId;
    }

    public boolean verifyEmail(VerifyEmailRequest request) {
        VerifyEmailCode verifyEmailCode = verifyEmailCodeStorage.get(request.getVerifyId())
            .orElseThrow(VerifyEmailCodeNotFoundException::new);

        int remainAttempt = verifyEmailCode.remainAttempt() - 1;
        if (remainAttempt == 0) {
            throw new VerifyEmailCodeEndAttemptException();
        }

        if (Objects.equals(verifyEmailCode.verifyCode(), request.getVerifyCode())) {
            verifyEmailCodeStorage.delete(request.getVerifyId());
            userInfoService.enabledUserInfo(verifyEmailCode.userId(), true);
            return true;
        }

        VerifyEmailCode updateVerifyEmailCode = new VerifyEmailCode(
            verifyEmailCode.userId(), verifyEmailCode.verifyCode(), remainAttempt
        );

        if (!verifyEmailCodeStorage.update(request.getVerifyId(), updateVerifyEmailCode)) {
            throw new VerifyEmailCodeNotFoundException();
        }
        throw new VerifyEmailCodeInvalidCodeException();
    }

    public void refreshCode(RefreshVerifyCodeRequest request, Locale locale) {
        VerifyEmailCode verifyEmailCode = verifyEmailCodeStorage.get(request.getVerifyId())
            .orElseThrow(VerifyEmailCodeNotFoundException::new);
        UserInfoDto userInfo = userInfoService.findById(verifyEmailCode.userId());

        String verifyCode = getVerifyCode();
        VerifyEmailCode refreshVerifyEmailCode = new VerifyEmailCode(verifyEmailCode.userId(), verifyCode, emailProperties.verifyEmailCode().attempt());
        verifyEmailCodeStorage.save(request.getVerifyId(), refreshVerifyEmailCode, Duration.ofMinutes(emailProperties.verifyEmailCode().minuteTime()));

        sendEmail(userInfo.email(), verifyCode, locale);
    }

    public void handleExpireId(String expireId) {
        try {
            UserInfoDto userInfo = userInfoService.findById(getUserIdFromVerifyId(expireId));
            if (!userInfo.enabled()) {
                userInfoService.deleteById(userInfo.id());
            }
        } catch (Exception _) {
            log.error("Couldn't delete user from the system by expireId: {}", expireId);
        }
    }

    public void cancelVerifyEmail(String verifyId) {
        verifyEmailCodeStorage.delete(verifyId);
        handleExpireId(verifyId);
    }

    private void sendEmail(String userEmail, String verifyCode, Locale locale) {
        publisher.publishEvent(new FromResourceEmailEvent(
            userEmail,
            "email.verify.subject",
            "email-verify",
            Map.of(
                "verifyCode", verifyCode,
                "expiresInMinutes", emailProperties.verifyEmailCode().minuteTime()
            ),
            locale
        ));
    }

    private String getVerifyCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    private String generateVerifyId(UUID userId) {
        return "%s:%s".formatted(userId.toString(), UUID.randomUUID());
    }

    private String getUserIdFromVerifyId(String verifyId) {
        return verifyId.split(":")[0];
    }
}
