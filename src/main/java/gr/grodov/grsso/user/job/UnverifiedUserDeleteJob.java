package gr.grodov.grsso.user.job;

import gr.grodov.grsso.common.props.EmailAppProperties;
import gr.grodov.grsso.user.domain.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnverifiedUserDeleteJob {
    private final EmailAppProperties appProperties;
    private final UserInfoRepo userInfoRepo;

    @Scheduled(cron = "${grsso.email.verify-email-code.check-cron}")
    @Transactional
    public void deleteExpiredUnconfirmedUsers() {
        log.debug("delete expire unconfirmed users");
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(appProperties.verifyEmailCode().minuteTime()));
        userInfoRepo.deleteByEnabledFalseAndCreatedAtBefore(cutoff);
    }
}
