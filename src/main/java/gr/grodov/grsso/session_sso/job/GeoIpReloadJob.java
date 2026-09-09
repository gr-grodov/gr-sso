package gr.grodov.grsso.session_sso.job;

import gr.grodov.grsso.common.props.GeoIpAppProperties;
import gr.grodov.grsso.session_sso.service.GeoLocationResolverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeoIpReloadJob {

    private final GeoLocationResolverService resolver;

    @Scheduled(cron = "${grsso.geoip.reload-cron}")
    @Transactional
    public void deleteExpiredUnconfirmedUsers() {
        try {
            resolver.reload();
        } catch (IOException ex) {
            log.error("Failed to reload GeoIP database", ex);
        }
    }
}