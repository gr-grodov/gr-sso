package gr.grodov.grsso.user.job;

import gr.grodov.grsso.common.props.VerifyEmailAppProperties;
import gr.grodov.grsso.user.domain.repo.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UnverifiedUserDeleteJob {
    private final VerifyEmailAppProperties appProperties;
    private final UserInfoRepo userInfoRepo;

    @Scheduled(cron = "${grsso.verify-email.check-cron}")
    @Transactional
    public void deleteExpiredUnconfirmedUsers() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>> start UnverifiedUserDeleteJob");
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(appProperties.minuteTime()));
        userInfoRepo.deleteByEnabledFalseAndCreatedAtBefore(cutoff);
    }
}
